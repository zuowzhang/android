### Simple Router for Android
* 支持activity/service/broadcast路由跳转
* 支持参数(path参数/Intent参数)自动解析，类似于spring @autowired，参数类型支持基本数据类型, Serializable, Parcelable

### Sample
```java
//初始化路由
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RouterManager.get().init(this);
    }
}

//定义中跳转过程中的自定义参数类型（Serializable/Parcelable）
public class Student implements Serializable {
    String name;
    int age;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

//定义目标路由地址
/***
* 通过@Param声明需要自动初始化的参数，参数必须不是[private]类型
*/

@Route(path = "/activity/second")
public class SecondActivity extends Activity {
    private static final String tag = "SecondActivity";

    private static final int requestCode = 1;

    Button go2Third;

    @Param
    String name;

    @Param
    int age;

    @Param
    char ch;

    @Param
    Student student;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        go2Third = findViewById(R.id.go2Third);
        go2Third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.get().go(new RoutePayload.Builder("router://my/third?ch=T")
                        .requestCode(SecondActivity.this, requestCode)
                        .action("myaction")
                        .addParam("name", "li")
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());
            }
        });
        RouteParamInjector.inject(this, getIntent());
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(tag, "requestCode = " + requestCode + "; resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

//同样，如果目标路由地址是一个Service，配置同Activity
@Route(path = "/service/demo")
public class DemoService extends Service {

    private static final String tag = "DemoService";

    @Param
    String name;

    @Param
    int age;

    @Param
    char ch;

    @Param
    Student student;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RouteParamInjector.inject(this, intent);
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

//跳转
public class MainActivity extends AppCompatActivity {

    private static final String tag = "MainActivity";

    Button go2Second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        go2Second = findViewById(R.id.go2Second);

        go2Second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Student student = new Student();
                student.name = "zzz";
                student.age = 18;

                RouterManager.get().go(new RoutePayload.Builder("/activity/second?ch=A")//path参数
                        .addParam("name", "li")//Intent参数
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());

                RouterManager.get().go(new RoutePayload.Builder("/service/demo?ch=S")
                        .type(RouteType.SERVICE)
                        .addParam("name", "li")
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());
            }
        });
    }
}
```
#### 可参考工程app