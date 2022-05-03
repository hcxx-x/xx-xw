[TOC]



# 一、写在前面

## 1、关于springboot集成logback或log4j2的问题

建议有什么问题先看这一章

	### 1.1、springboot 默认框架

springboot默认集成的日志框架是logback，如果选择使用logback，则无需额外引入logback的依赖，一般在引入spring-boot-start相关依赖的时候就会默认的将logback的依赖引入到项目中

### 1.2、springboot中使用log4j2需要处理的问题

logback和log4j2不能同时使用，如果项目需要使用log4j2作为日志框架，则需要将logback的相关依赖排除，并引入log4j2相关的依赖

1. 排除依赖

   ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- 去掉springboot默认配置（logback）,若使用logback 日志处理方式则这里不用排除该默认设置 -->
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
   </dependency>
   ```

2. 引入依赖

   ```xml
    <!-- 引入log4j2依赖，若使用log4j进行日志处理，则需要排除上面所描述的springboot的默认日志依赖，否则不会生效-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
   ```

### 1.3 log4j2的异步输出问题

如果需要使用log4j2的异步日志输出则需要额外再引入一个依赖，并且需要注意版本问题（注意：以下版本匹配问题并没有经过自己的实际验证，只是看到有博客这些写）


> Log4j-2.9及更高版本在类路径上需要disruptor-3.3.4.jar或更高版本。在Log4j-2.9之前，需要disruptor-3.0.0.jar或更高版本。


```xml
 <!-- 用于支持log4j2的异步日志输出 -->
 <dependency>
     <groupId>com.lmax</groupId>
     <artifactId>disruptor</artifactId>
 </dependency>
```

### 1.4 关于log4j2的高亮显示

注意：关于log4j2的高亮显示问题，这一次并没有遇到，不知道是这次测试的时候使用的springboot版本比较高还是什么（本次测试的springboot版本为2.6.7），但是这里还是把之前的解决方案放在这里，如果遇到类似的问题，可以参考解决



新版的log4j2不能向logback一样直接进行高亮显示，需要进行一定的配置，配置方式有两种

1、在 VM options 中添加 -Dlog4j.skipJansi=false

2、在resource 目录下创建 log4j2.component.properties 文件，并在文件中添加以下配置

```
LOG4J_SKIP_JANSI=false
```

# 二、logback

## 1、在yaml配置文件中配置日志

springboot 默认使用的日志框架是logback，在yml配置文件中也可以对logback进行简单的配置，可配置内容如下：

```yaml
logging:
  # 日志级别，可以为每个包配置不同的日志输出级别，当代码中的日志输出级别大于这个配置时，对应的日志才会被输出（eg: com.xx包的日志打印级别为waring,则该包下所有代码中的debug和info级别的日志不会被打印，而error和fatal级别的日志则会被打印）
  level:
    root: warn
    com.xx: info
  file:
    # logging.file.path好像没有什么用，至少在我这次测试中是没有起到作用的，无论配置是D:/weblogs 还是 ../weblogs (不生效的原因：path和file二者不能同时使用，如若同时使用，则只有logging.file生效)
    # path: D:/weblogs
    # logging.file.name 可以是一个文件名称，也可以是一个路径，路径以“/"开头则表示根路径，路径不以“/"开头则表示相对路径（在windows环境中，非jar包运行时，会从最上一层的父级目录开始生成对应的文件夹以及文件）
    name: wxlogs/web.log
  logback:
    rollingpolicy:
      clean-history-on-start: false
      # 滚动日志历史文件命名规则 默认：${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz，注意：日期中不能出现“:” (如果配置使用双引号包裹起来应该可以，下面的配置也有类似问题) 否则会使配置失效，如果出现时分秒，可以使用其他符号连接，而不要使用“:”
      file-name-pattern: wxlogs/web.%d{yyyy-MM-dd-HH-mm-ss}.%i.log
      # 日志保留最大期限，该配置和 file-name-pattern配置有关，如果file-name-pattern精确到天，则max-history则为日志保留的最大天数，如果file-name-pattern精确到秒，则max-history则为日志保留的最大秒数
      max-history: 7
      max-file-size: 10KB
      total-size-cap: 1GB

  # group 可以对代码以包的形式或者类的形式进行分组，如下 xxxw: com.xx 表示的含义就是为com.xx这个包单独封一个组，xxxw就是这个组名，分完组之后对于logging.level的配置就可以不指定包名，直接指定组名就可以了 如：logging.level.xxxw: debug
  # springboot 预定义了一些组 可以在官方文档中查询  地址：https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging
  group:
    xxxw: com.xx
  pattern:
    #在控制台输入日志格式，默认值："%clr(%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${logging.pattern.level:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${logging.exception-conversion-word:-%wEx}"
    console: "%clr(%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${logging.pattern.level:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${logging.exception-conversion-word:-%wEx}"
    # 在日志文件中输出的日志格式，默认值  "%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}} ${logging.pattern.level:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${logging.exception-conversion-word:-%wEx}"
    file: "%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}} ${logging.pattern.level:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${logging.exception-conversion-word:-%wEx}"
    # 配置时间格式化样式 默认值 "yyyy-MM-dd HH:mm:ss.SSS"
    dateformat: "yyyy-MM-dd HH:mm:ss.SSS"
    # 渲染日志级别时使用的格式 默认 “%5p”
    level: "%5p"
  # 定义当日志发生异常时的转换字 (没明白啥意思)
  exception-conversion-word: "%wEx"
  charset:
    # 配置控制台日志输出的字符集编码
    console: UTF-8
    # 配置日志文件输出的字符集编码
    file: UTF-8
  # 在yml文件中只能对日志进行简单的配置.如果需要更加细致的配置则需要使用特定的日志配置文件，logging.config 指定配置文件的名称 (spring官方推荐使用带-spring后缀的文件)，下面的配置值没有经过验证！
  # 这项配置可以不配置，springboot 会自动去查找classpath下的 logback配置（logback.xml,logback-spring.xml,logback-spring.groovy、 logback.groovy); log4j2配置 (log4j2-spring.xml、log4j2.xml); Java Util Logging 配置（logging.properties）
  config: classpath:logback-spring.xml


```

##  2、在配置文件中配置logback

springboot 默认会扫描类路径下的 `logback.xml` `logback-spring.xml` `logback-spring.grovvy` `loback.grovy`，推荐使用`logback-spring.xml` 或者 `logback-spring.grovy` 命名的配置文件（官方推荐）

> When possible, we recommend that you use the `-spring` variants for your logging configuration (for example, `logback-spring.xml` rather than `logback.xml`). If you use standard configuration locations, Spring cannot completely control log initialization.



### 2.1 一个可以拿过来就用的logback-spring.xml文件内容

```xml
<!--debug="true" : 打印 logback 内部状态（默认当 logback 运行出错时才会打印内部状态 ）, 配置该属性后打印条件如下（同时满足）：
    1、找到配置文件 2、配置文件是一个格式正确的xml文件 也可编程实现打印内部状态, 例如： LoggerContext lc = (LoggerContext)
    LoggerFactory.getILoggerFactory(); StatusPrinter.print(lc); -->
<!-- scan="true" ： 自动扫描该配置文件，若有修改则重新加载该配置文件 -->
<!-- scanPeriod="30 seconds" : 配置自动扫面时间间隔（单位可以是：milliseconds, seconds, minutes
    or hours，默认为：milliseconds）， 默认为1分钟，scan="true"时该配置才会生效 -->
<configuration debug="false" scan="false" scanPeriod="60 seconds">
    <!-- 设置变量。定义变量后，可以使“${}”来使用变量。 -->
    <property name="LOG_HOME" value="./logs/logback"/>
    <property name="sql_log_level" value="info"/>


    <!--从springboot 配置文件中读取属性值 scope表示作用域， name为变量名称，source指定springboot配置文件中的某个配置项，defaultValue表示没有读取到对应配置时的默认值-->
    <springProperty scope="context" name="appName" source="spring.application.name"  defaultValue="app"/>

    <!-- 设置 logger context 名称,一旦设置不可改变，默认为default -->
    <contextName>myAppName</contextName>

    <!-- 定义控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!--encoder 和 layout 选用一个即可，不过自0.9.19版本之后，极力推荐使用encoder-->
        <!-- encoder class为空时, 默认也为 ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出，并美化日（加颜色） 其中%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <!--支持的颜色字符编码 %black 黑色、 %red 红色、 %green 绿色、 %yellow 黄色、 %blue 蓝色、 %magenta 洋红色、 %cyan 青色、 %white 白色、 %gray 灰色
            以下为对应加粗的颜色代码 %boldRed、 %boldGreen、 %boldYellow、 %boldBlue、 %boldMagenta、 %boldCyan、 %boldWhite、 %highlight 高亮色-->
            <pattern>%boldGreen(${appName})-%red(%date{yyyy-MM-dd HH:mm:ss}) %highlight(%-5level) %red([%thread]) %boldMagenta(%logger{50}) %cyan(%msg%n)</pattern>
            <!--<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} - [%juc.thread] - %-5level - %logger{50} - %msg%n</pattern>-->
            <!--<pattern>%d{HH:mm:ss.SSS} [%juc.thread] %-5level %logger{36} - %msg%n</pattern>-->
        </encoder>

        <!--<layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%clr(%d{yyyy-MM-dd HH:mm:ss.SSS} - [%juc.thread] - %-5level - %logger{50} - %msg%n)</pattern>
        </layout>-->
    </appender>

    <!--日志按照时间滚动生成滚动生成info级别的日志-->
    <appender name="LOG_FILE_INFO_APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 指定当前活跃的日志文件的名称 -->
        <file>${LOG_HOME}/${appName}-info.log</file>

        <!--过滤器，以当前配置为例，level配置的是info, onMatch配置的是ACCEPT则表示如果日志级别时info就打印，onMismatch配置的事DENY 则表示日志级别非info就不打印-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <!--日志滚动策略，按照时间进行滚动-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--达到要求后将当前活跃的日志文件以下面的格式进行重命名, %i 表示序号，从0开始依次递增，
            具体含义是如果当天内日志文件达到最大size，则根据当前日期以及序号重名老的日志文件，
            注意若是不配置下面的timeBasedFileNamingAndTriggeringPolicy
            或者 不将上面的 TimeBasedRollingPolicy 换成 SizeAndTimeBasedRollingPolicy 则 ‘%i’ 可能会出现解析错误问题-->
            <!--若文件格式是zip或者gz则会自动压缩，如果不是则不会自动压缩-->
            <fileNamePattern>${LOG_HOME}/${appName}-info-%d{yyyy-MM-dd}-%i.log.zip</fileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
            <!-- 日志总保存量为10GB -->
            <totalSizeCap>10GB</totalSizeCap>
            <!--在根据日期来滚动生成的基础上配置每个日志文件的最大size,如果超出这个size，则重新开启一个新的日志文件，之前老的文件将会被按照上面的命名规则重命名保存下来-->
            <!--
                除了这种配置方式以外，还可以将上面的 rollingPolicy 标签中的class的属性值设置为ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
                这样的话，就不需要下面的timeBasedFileNamingAndTriggeringPolicy标签了，但是需要在rollingPolicy标签中设置子标签 MaxFileSize
             -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <MaxFileSize>10MB</MaxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>

        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </encoder>
    </appender>


    <!--日志按照时间滚动生成滚动生成warn级别的日志-->
    <appender name="LOG_FILE_WARN_APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 指定当前活跃的日志文件的名称 -->
        <file>${LOG_HOME}/${appName}-warn.log</file>

        <!--过滤器，以当前配置为例，level配置的是info, onMatch配置的是ACCEPT则表示如果日志级别时info就打印，onMismatch配置的事DENY 则表示日志级别非info就不打印-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <!--日志滚动策略，按照时间进行滚动-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--达到要求后将当前活跃的日志文件以下面的格式进行重命名, %i 表示序号，从0开始依次递增，
            具体含义是如果当天内日志文件达到最大size，则根据当前日期以及序号重名老的日志文件，
            注意若是不配置下面的timeBasedFileNamingAndTriggeringPolicy
            或者 不将上面的 TimeBasedRollingPolicy 换成 SizeAndTimeBasedRollingPolicy 则 ‘%i’ 可能会出现解析错误问题-->
            <!--若文件格式是zip或者gz则会自动压缩，如果不是则不会自动压缩-->
            <fileNamePattern>${LOG_HOME}/${appName}-warn-%d{yyyy-MM-dd}-%i.log.zip</fileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
            <!-- 日志总保存量为10GB -->
            <totalSizeCap>10GB</totalSizeCap>
            <!--在根据日期来滚动生成的基础上配置每个日志文件的最大size,如果超出这个size，则重新开启一个新的日志文件，之前老的文件将会被按照上面的命名规则重命名保存下来-->
            <!--
                除了这种配置方式以外，还可以将上面的 rollingPolicy 标签中的class的属性值设置为ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
                这样的话，就不需要下面的timeBasedFileNamingAndTriggeringPolicy标签了，但是需要在rollingPolicy标签中设置子标签 MaxFileSize
             -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <MaxFileSize>10MB</MaxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>

        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </encoder>
    </appender>



    <!--日志按照时间滚动生成滚动生成error级别的日志-->
    <appender name="LOG_FILE_ERROR_APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 指定当前活跃的日志文件的名称 -->
        <file>${LOG_HOME}/${appName}-error.log</file>

        <!--过滤器，以当前配置为例，level配置的是info, onMatch配置的是ACCEPT则表示如果日志级别时info就打印，onMismatch配置的事DENY 则表示日志级别非info就不打印-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>error</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <!--日志滚动策略，按照时间进行滚动-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--达到要求后将当前活跃的日志文件以下面的格式进行重命名, %i 表示序号，从0开始依次递增，
            具体含义是如果当天内日志文件达到最大size，则根据当前日期以及序号重名老的日志文件，
            注意若是不配置下面的timeBasedFileNamingAndTriggeringPolicy
            或者 不将上面的 TimeBasedRollingPolicy 换成 SizeAndTimeBasedRollingPolicy 则 ‘%i’ 可能会出现解析错误问题-->
            <!--若文件格式是zip或者gz则会自动压缩，如果不是则不会自动压缩-->
            <fileNamePattern>${LOG_HOME}/${appName}-error-%d{yyyy-MM-dd}-%i.log.zip</fileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
            <!-- 日志总保存量为10GB -->
            <totalSizeCap>10GB</totalSizeCap>
            <!--在根据日期来滚动生成的基础上配置每个日志文件的最大size,如果超出这个size，则重新开启一个新的日志文件，之前老的文件将会被按照上面的命名规则重命名保存下来-->
            <!--
                除了这种配置方式以外，还可以将上面的 rollingPolicy 标签中的class的属性值设置为ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
                这样的话，就不需要下面的timeBasedFileNamingAndTriggeringPolicy标签了，但是需要在rollingPolicy标签中设置子标签 MaxFileSize
             -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <MaxFileSize>10MB</MaxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>

        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </encoder>
    </appender>


    <!-- 异步输出info级别的日志 -->
    <appender name="ASYNC_LOG_FILE_INFO_APPENDER" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志  默认情况下如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>256</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="LOG_FILE_INFO_APPENDER"/>
    </appender>

    <appender name="ASYNC_LOG_FILE_WARN_APPENDER" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>256</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="LOG_FILE_WARN_APPENDER"/>
    </appender>

    <appender name="ASYNC_LOG_FILE_ERROR_APPENDER" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>256</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="LOG_FILE_ERROR_APPENDER"/>
    </appender>


    <!--
        name: 用来指定受此 logger 约束的某一个包或者具体的某一个类
        level:用来设置打印级别（日志级别），大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              还有一个特俗值INHERITED或者同义词NULL，代表强制执行上级的级别。
              如果未设置此属性，则继承最近的父 logger（该logger需显示定义level,直到rootLogger）的日志级别。
        addtivity: logger 的 appender 默认具有累加性（默认日志输出到当前logger的appender和所有祖先logger的appender中,
                   如果在当前logger 和root中同时配置同一个appender，则日志会打印两遍），
                   可通过配置 “additivity”属性修改默认行为是否向上级loger传递打印信息。默认是true。

        logger的祖先和父子级关系，根据定义循序，上面一个定义的logger是下面一个logger的父级，
        logger的name也可以提现出logger的父子级或者祖先关系，如 name 为x 的logger 是name为x.y的logger和 name为 x.y.z的祖先，同理x.y也是x.y.z的祖先
    -->
    <logger name="org.springframework" level="info"  additivity="false">
        <!--由于additivity="false" 所以对于org.springframework中的日志输出只会在名称为stdout 的appender 中出现，在本文件的配置就是只输出到控制台-->
        <!--<appender-ref ref="STDOUT"/>-->
    </logger>

    <!--使用p6spy打印完整的sql日志-->
    <logger name="com.p6spy.engine.spy.appender.Slf4JLogger" level="debug" additivity="false">
        <!--dditivity="false"，所以没有从祖先或者父级那里继承任何appender，如果不单独引用appender则com.p6spy.engine.spy.appender.Slf4JLogger类下面的日志不会被打印-->
        <!--单独定义了appender-ref,所以即使dditivity="false"  com.p6spy.engine.spy.appender.Slf4JLogger类相关的日志也会输出到  STDOUT appender中-->
        <appender-ref ref="STDOUT"/>
    </logger>
    <!-- 更多常用的三方框架logger配置如下 -->
    <!-- 需要覆盖日志级别,减少不关注的日志输出 -->
    <logger name="sun.rmi" level="error"/>
    <logger name="sun.net" level="error"/>
    <logger name="javax.management" level="error"/>
    <logger name="org.redisson" level="warn"/>
    <logger name="com.zaxxer" level="warn"/>


    <!--root也是<logger>元素，但是它是根logger，是所有logger的祖先，所有包下面的日志都会被记录。可以包含零个或多个<appender-ref>元素，标识这个appender将会添加到这个logger。-->
    <root level="info" additivity="false">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="ASYNC_LOG_FILE_INFO_APPENDER"/>
        <appender-ref ref="ASYNC_LOG_FILE_WARN_APPENDER"/>
        <appender-ref ref="ASYNC_LOG_FILE_ERROR_APPENDER"/>
    </root>

</configuration>

```



# 三、log4j2

## 1.1 一个可以拿过来就用的log4j2-spring.xml的文件内容

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--这个配置文件好像不区分大小写，然后还会吧中划线专驼峰？这个配置文件中各种命名格式都有，居然没有报错，使用的时候还需要注意下-->
<!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
<!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，你会看到log4j2内部各种详细输出-->
<!--monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数-->
<configuration status="WARN" monitorInterval="60">

    <!--变量配置-->
    <Properties>
        <!-- 格式化输出：%date表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %msg：日志消息，%n是换行符-->
        <!-- %logger{36} 表示 Logger 名字最长36个字符 -->
        <!--%highlight{}表示将{}中的内容高亮显示，但是自log4j 2.10版本以后该功能默认是关闭的，若要开启需要在 VM options 中添加 -Dlog4j.skipJansi=false即可-->
        <!--<property name="LOG_PATTERN" value="%date{HH:mm:ss.SSS} %highlight{[%thread]} %-5level %logger{36} - %msg%n" />-->
        <!--可以通过%style, %highlight,%clr 对日志的输出格式进行个性化 具体配置方式可以查看官方文档，如果官方文档中没有找到，可以参考这个链接（有可能会被删除）https://www.jianshu.com/p/b6409b3042e2-->
        <property name="LOG_PATTERN" value="%style{%clr{%date{yyyy-MM-dd HH:mm:ss}}{red}}{bright,BG_Yellow} %clr{%5p}{FATAL=red, ERROR=red, WARN=yellow, INFO=green, DEBUG=green, TRACE=green} %clr{[%thread]}{red} %style{%clr{%logger{50}}{magenta}}{bright} %clr{%msg%n}{cyan}" />
        <!-- 定义日志存储的路径 -->
        <property name="FILE_PATH" value="./logs/log4j2" />
        <property name="FILE_NAME" value="lzy-log4j2-demo" />
    </Properties>

    <appenders>
        <console name="Console" target="SYSTEM_OUT">
            <!--输出日志的格式-->
            <PatternLayout  pattern="${LOG_PATTERN}"/>
            <!--控制台只输出level及其以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
        </console>

        <!--文件会打印出所有信息，append表示是已追加模式添加日志，如果是true则表示追加，如果是false则每次启动程序以前的日志会被清空，默认true-->
        <File name="Filelog" fileName="${FILE_PATH}/test.log" append="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </File>

        <!-- 这个会打印出所有的info及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileInfo" fileName="${FILE_PATH}/info.log" filePattern="${FILE_PATH}/${FILE_NAME}-INFO-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval属性用来指定多久滚动一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 这个会打印出所有的warn及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileWarn" fileName="${FILE_PATH}/warn.log" filePattern="${FILE_PATH}/${FILE_NAME}-WARN-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!-- Policies:指定滚动日志的策略，就是什么时候进行新建日志文件输出日志.
                TimeBasedTriggeringPolicy:Policies子节点，基于时间的滚动策略，interval属性用来指定多久滚动一次，默认是1 hour。modulate=true用来调整时间：比如现在是早上3am，interval是4，那么第一次滚动是在4am，接着是8am，12am...而不是7am.
                SizeBasedTriggeringPolicy:Policies子节点，基于指定文件大小的滚动策略，size属性用来定义每个日志文件的大小.-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 这个会打印出所有的error及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileError" fileName="${FILE_PATH}/error.log" filePattern="${FILE_PATH}/${FILE_NAME}-ERROR-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval属性用来指定多久滚动一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>


        <!--异步AsyncAppender进行配置直接引用上面的Console的name-->
        <Async name="Async">
            <AppenderRef ref="Console"/>
        </Async>
    </appenders>

    <!--Logger节点用来单独指定日志的形式，比如要为指定包下的class指定不同的日志级别等。-->
    <!--然后定义loggers，只有定义了logger并引入的appender，appender才会生效-->
    <loggers>
        <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
        <!--若是additivity设为false，则 子Logger 只会在自己的appender里输出，而不会在 父Logger 的appender里输出。-->
        <logger name="org.mybatis" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </logger>

        <root level="info">
            <!--此处如果引用异步AsyncAppender的name就是异步输出日志-->
            <!--需要注意的是这种配置方式 采用了ArrayBlockingQueue来保存需要异步输出的日志事件，效率并没有另外一种配置方式高-->
           <!-- <Appender-Ref ref="Async"/>-->
            <!--此处如果引用Appenders标签中Console的name就是同步输出日志-->
            <appender-ref ref="Console"/>
            <appender-ref ref="Filelog"/>
            <appender-ref ref="RollingFileInfo"/>
            <appender-ref ref="RollingFileWarn"/>
            <appender-ref ref="RollingFileError"/>
        </root>

        <!--异步日志输出 需要在pom文件中引入disruptor的依赖（Log4j-2.9及更高版本在类路径上需要disruptor-3.3.4.jar或更高版本。在Log4j-2.9之前，需要disruptor-3.0.0.jar或更高版本。）-->
        <!--  <AsyncLogger name="org.springframework" level="info" additivity="false">
              <AppenderRef ref="Console"/>
          </AsyncLogger>-->
        <!--可以通过以下方法配置所有的appender为异步输出-->
        <!-- <asyncRoot level="info">
            <appender-ref ref="Console"/>
            <appender-ref ref="Filelog"/>
            <appender-ref ref="RollingFileInfo"/>
            <appender-ref ref="RollingFileWarn"/>
            <appender-ref ref="RollingFileError"/>
        </asyncRoot>-->

        <!--
            关于异步日志的多一点说明： 上面的AsyncLogger和 asyncRoot标签，可以用来将日志的异步记录和同步记录进行混用
            除此之外还有一种完全异步的使用方式，推荐使用这种方式进行异步日志打印（不知道为什么推荐，难道性能更高？），配置方式如下
            将系统属性log4j2.contextSelector设置 为org.apache.logging.log4j.core.async.AsyncLoggerContextSelector将会使所有的记录器异步
            设置方式：
            1、在resource目录下创建log4j2.component.properties，并添加以下内容
                # 设置异步日志系统属性
                log4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
            2、如果使用的事springboot项目，则可以在启动的main方法中添加如下代码（是不是放在第一行需要再测试）
                System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            当配置AsyncLoggerContextSelector作为异步日志时，请确保在配置中使用普通的 <root>和<logger>元素。
            AsyncLoggerContextSelector将确保所有记录器都是异步的，使用的机制与配置<asyncRoot> 或<asyncLogger>时的机制不同。

            通过log.info(“是否为异步日志：{}”, AsyncLoggerContextSelector.isSelected());可以查看是否为异步日志。

            sync：同步打印日志，日志输出与业务逻辑在同一线程内，当日志输出完毕，才能进行后续业务逻辑操作
            Async Appender：异步打印日志，内部采用ArrayBlockingQueue，对每个AsyncAppender创建一个线程用于处理日志输出。
            Async Logger：异步打印日志，采用了高性能并发框架Disruptor，创建一个线程用于处理日志输出。
        -->
    </loggers>

</configuration>


```

## 2、关于log4j2的异步日志输出

log4j2的日志输出可以有几种方案来供选择，同步输出、同步异步混用输出、异步输出（推荐，效率高）

### 2.1 同步输出

不加任何异步配置的方式就是同步输出，没有什么好说的

### 2.2 混用输出

#### 2.2.1 使用Async Appender实现日志的异步打印

内部使用的一个队列（ArrayBlockingQueue）和一个后台线程（对每个AsyncAppender创建一个线程用于处理日志输出），日志先存入队列，后台线程从队列中取出日志。阻塞队列容易受到锁竞争的影响，当更多线程同时记录时性能可能会变差。

使用方式：

​	 在appenders标签内创建子标签Async，并在Async标签中引用一个已经定义好的appender，然后再logger标签中 使用刚刚定义的AsyncAppender即可（注意，如果引用的不是AsyncAppender则会使用同步的方式进行日志打印，所以说这种方式是混用，当然也可以通过配置的方式来实现完全的异步输出），具体配置方式如下

```xml
    <appenders>
        <!--文件会打印出所有信息，append表示是已追加模式添加日志，如果是true则表示追加，如果是false则每次启动程序以前的日志会被清空，默认true-->
        <File name="Filelog" fileName="${FILE_PATH}/test.log" append="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </File>

        <!--异步AsyncAppender进行配置直接引用上面的Console的name-->
        <Async name="Async">
            <AppenderRef ref="Filelog"/>
        </Async>
    </appenders>


    <loggers>
        <logger name="org.mybatis" level="info" additivity="false">
            <AppenderRef ref="Async"/>
        </logger>
    </loggers>


```



#### 2.2.2 使用Async Logger实现日志的异步打印

​	Async Logger。内部使用的是LMAX Disruptor技术，Disruptor是一个无锁的线程间通信库，它不是一个队列，不需要排队，从而产生更高的吞吐量和更低的延迟。

​	此种方式下异步日志的输出依赖于AsyncRoot、AsyncLogger标签。以上两个标签可以单独使用实现日志的纯异步输出（如果要实现日志的纯异步输出可以使另外一种方式，后面会说），也可以和Root、Logger混合配置，从而实现同步异步混合。但是需要注意，配置中只能有一个root元素，也就是只能使用AsyncRoot或Root中的一个。

使用步骤：

1、引入Disruptor依赖

​	注意，Log4j-2.9+需要disruptor-3.3.4.jar或更高版本。在Log4j-2.9之前，需要disruptor-3.0.0.jar或更高版本。

```xml
<!-- 用于支持log4j2的异步日志输出 -->
 <dependency>
     <groupId>com.lmax</groupId>
     <artifactId>disruptor</artifactId>
 </dependency>
```

2、使用AsyncRoot、AsyncLogger标签实现异步打印

```
 <!--Logger节点用来单独指定日志的形式，比如要为指定包下的class指定不同的日志级别等。-->
    <!--然后定义loggers，只有定义了logger并引入的appender，appender才会生效-->
    <loggers>
        <!--异步日志输出 需要在pom文件中引入disruptor的依赖（Log4j-2.9及更高版本在类路径上需要disruptor-3.3.4.jar或更高版本。在Log4j-2.9之前，需要disruptor-3.0.0.jar或更高版本。）-->
         <AsyncLogger name="org.springframework" level="info" additivity="false">
              <AppenderRef ref="Console"/>
          </AsyncLogger>
          
          
        <!--可以通过以下方法配置所有的appender为异步输出-->
        <asyncRoot level="info">
            <appender-ref ref="Console"/>
            <appender-ref ref="Filelog"/>
            <appender-ref ref="RollingFileInfo"/>
            <appender-ref ref="RollingFileWarn"/>
            <appender-ref ref="RollingFileError"/>
        </asyncRoot>
    </loggers>
```



### 2.3、纯异步输出

无论是 `Async Appender`还是 `Async Logger` 理论上来说都可以通过配置来实现日志的纯异步输出。但是不推荐，推荐使用下面这种方式

注意：以下说明建立在2.2.2章的基础上

#### 2.3.1 设置系统属性

将系统属性log4j2.contextSelector设置 为org.apache.logging.log4j.core.async.AsyncLoggerContextSelector将会使所有的记录器异步
   	设置方式(以下两种任选一种)：
            1、在resource目录下创建log4j2.component.properties，并添加以下内容

                ```properties
                log4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
                ```

​            2、如果使用的事springboot项目，则可以在启动的main方法中添加如下代码（是不是放在第一行需要再测试）

```java
  System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
```

#### 2.3.2 修改标签

将2.2.2中的`AsyncRoot`、`AsyncLogger` 标签换成对应的 `Root`、`Logger` 标签

开启全异步时，日志配置中需要使用普通的Root和Logger元素。如果使用了AsyncRoot或AsyncLogger，将产生不必要的开销。

当配置AsyncLoggerContextSelector作为异步日志时，请确保在配置中使用普通的 <root>和<logger>元素。AsyncLoggerContextSelector将确保所有记录器都是异步的，使用的机制与配置<asyncRoot> 或<asyncLogger>时的机制不同。

## 3、log4j2日志的彩色输出

在log4j2中可以通过配置，实现在支持ANSI 输出的控制台中输出彩色日志

可以实现彩色输出的语法有 

1、%highlight{内容}{具体颜色，可以根据不同的日志级别指定不同的颜色输出}、

2、%style{内容}{样式（多个样式以逗号隔开）}、

3、%clr{内容}{具体颜色，可以根据不同的日志级别指定不同的颜色输出}   注意：这个在log4j2的文档中没有找到对应的配置方式，但是我在测试中使用到了这个，也确实是高亮输出了，使用时需要注意

具体的配置方式可以查看官方文档   [Log4j – Log4j 2 Layouts (apache.org)](https://logging.apache.org/log4j/2.x/manual/layouts.html#enable-jansi)



启用彩色日志输出还需要对PatternLayout标签中的`disableAnsi `和`noConsoleNoAnsi`属性进行配置，如果不配置有可能打印不出来彩色日志，有关官方的说明可以看 3.2章节

```
 <!--输出日志的格式（彩色: 指定 disableAnsi、noConsoleNoAnsi 为 false 即可）-->
<PatternLayout disableAnsi="false" noConsoleNoAnsi="false" charset="UTF-8" pattern="${LOG_PATTERN}"/>
```





### 3.2 彩色输出可能存在的问题

官方说明：

> ANSI escape sequences are supported natively on many platforms but are not by default on Windows. To enable ANSI support add the [Jansi](https://jansi.fusesource.org/) jar to your application and set property to . This allows Log4j to use Jansi to add ANSI escape codes when writing to the console. `log4j.skipJansi``false`
>
> NOTE: Prior to Log4j 2.10, Jansi was enabled by default. The fact that Jansi requires native code means that Jansi can only be loaded by a single class loader. For web applications this means the Jansi jar has to be in the web container's classpath. To avoid causing problems for web applications, Log4j will no longer automatically try to load Jansi without explicit configuration from Log4j 2.10 onward.

翻译：

许多平台本机支持 ANSI 转义序列，但 Windows 默认不支持。要启用 ANSI 支持，请将 Jansi jar 添加到您的应用程序并将属性设置为 .这允许 Log4j 在写入控制台时使用 Jansi 添加 ANSI 转义码。 log4j.skipJansifalse

注意：在 Log4j 2.10 之前，Jansi 默认启用。 Jansi 需要本地代码这一事实意味着 Jansi 只能由单个类加载器加载。对于 Web 应用程序，这意味着 Jansi jar 必须位于 Web 容器的类路径中。为了避免给 Web 应用程序带来问题，Log4j 将不再自动尝试加载 Jansi，而无需从 Log4j 2.10 开始进行显式配置。
