import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Optionals的相关新特性
 */
public class OptionalNewFeature {
    public static void main(String[] args) {
        // ifPresentOrElse 如果元素不为空执行前面的的lambda表达式，如果为空执行后面的Runnable的run方法
        Optional.ofNullable(null).ifPresentOrElse(e -> {
            System.out.println("元素不为空");
        }, ()->{
            System.out.println("元素为空");
        });

        // optional转换成stream流，如果Optional中包裹的元素为空则会传化为一个空流
        System.out.println(Optional.ofNullable(null).stream().collect(Collectors.toSet()));

        // 测试var关键字的使用
        testVar();
    }

    /**
     * jdk11 之后可以允许使用var定义局部变量，编译器会自动进行类型推断
     */
    public static void testVar(){
        var str = "str";
        System.out.println(str);
    }
}
