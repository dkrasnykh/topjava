<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link type="text/css"
          href="css/ui-lightness/jquery-ui-1.8.18.custom.css" rel="stylesheet"/>
    <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.18.custom.min.js"></script>
    <title>Add new meal</title>
</head>
<body>

<form method="POST" action='meals' name="frmAddMeal">
    <input type="hidden" readonly="readonly" name="id"
           value="${meal.id}"/> <br/>
    <table>
        <tbody>
        <tr>
            <td>Date:</td>
            <td>
                <input type="datetime-local" name="dateTime" value="${meal.dateTime}"/> <br/>
            </td>
        </tr>
        <tr>
            <td>Description:</td>
            <td>
                <input type="text" name="description" value="${meal.description}"/> <br/>
            </td>
        </tr>
        <tr>
            <td>Calories:</td>
            <td>
                <input type="number" name="calories" value="${meal.calories}"/> <br/>
            </td>
        </tr>
        </tbody>
    </table>
    <input type="submit" value="Submit"/>
    <input type="button" value="Cancel" onclick="javascript:history.go(-1)">
</form>
</body>
</html>