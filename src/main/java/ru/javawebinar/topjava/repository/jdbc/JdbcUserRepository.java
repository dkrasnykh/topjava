package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    static class Extractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<User> result = new ArrayList<>();
            Map<Integer, Set<Role>> map = new HashMap<>();
            while (rs.next()) {
                Integer userId = Integer.parseInt(rs.getString("id"));
                Role role = Role.valueOf(rs.getString("role"));
                User user = new User(userId, rs.getString("name"), rs.getString("email"), rs.getString("password"), role);
                user.setCaloriesPerDay(Integer.parseInt(rs.getString("calories_per_day")));
                if (rs.getString("enabled").equals("t")) {
                    user.setEnabled(Boolean.TRUE);
                } else {
                    user.setEnabled(Boolean.FALSE);
                }
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    String registred = rs.getString("registered").substring(0, 19);
                    user.setRegistered(formatter.parse(registred));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!result.contains(user)) {
                    result.add(user);
                }
                Set<Role> temp = map.putIfAbsent(userId, new HashSet<>());
                if (temp == null) {
                    temp = new HashSet<>();
                }
                temp.add(role);
                map.put(userId, temp);
            }
            for (User user : result) {
                user.setRoles(map.get(user.getId()));
            }
            return result;
        }
    }

    private final Extractor RESULT_SET_EXTRACTOR = new Extractor();

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            insertRoles(user.getId(), List.of(user.getRoles().toArray(new Role[0])));

        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
            insertRoles(user.getId(), List.of(user.getRoles().toArray(new Role[0])));

            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            }
        }
        return user;
    }

    public void insertRoles(Integer userId, List<Role> roles){
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, userId);
                ps.setString(2, roles.get(i).name());
            }

            @Override
            public int getBatchSize() {
                return roles.size();
            }
        });
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users AS u LEFT JOIN user_roles AS r ON u.id = r.user_id WHERE u.id=?", RESULT_SET_EXTRACTOR, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users AS u LEFT JOIN user_roles AS r ON u.id = r.user_id WHERE email=?", RESULT_SET_EXTRACTOR, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users AS u LEFT JOIN user_roles AS r ON u.id = r.user_id ORDER BY u.name, u.email", RESULT_SET_EXTRACTOR);
    }
}
