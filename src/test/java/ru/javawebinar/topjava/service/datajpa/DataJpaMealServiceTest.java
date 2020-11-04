package ru.javawebinar.topjava.service.datajpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class DataJpaMealServiceTest extends AbstractMealServiceTest {
}
