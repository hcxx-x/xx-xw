package com.xx.web.test;




import java.util.List;

@Slf4j
@SpringBootTest()
public class MyBatisPlusTest {

    @Autowired
    TestActableMapper testActableMapper;

    @Test
    public void testSelect(){
        try {
            List<TestActable> testActables = testActableMapper.selectList(new LambdaQueryWrapper<>());
            log.info("查询结果：{}",testActables);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Autowired
    private TestService testService;

    @Autowired
    private TestMapper testMapper;

    @Test
    public void testTableLogin(){
        testService.removeById(1);
        testService.removeById(2);
        testService.removeById(3);
        testService.removeById(4);

        System.out.println(testMapper.getById(4L));
    }
}
