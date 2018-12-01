### Simple Router for Android
* 支持activity/service/broadcast路由跳转
* 支持参数自动解析，类似于spring @autowired，参数类型支持基本数据类型, Serializable, Parcelable

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

//定义目标路由地址
/***
* 通过@Param声明需要自动初始化的参数
* 
*/

@Route(path = "/activity/second")
public class SecondActivity extends Activity {
    private static final String tag = "SecondActivity";

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
        RouteParamInjector.inject(this, getIntent());//自动初始化被@param声明的参数
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
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
```
#### 可参考工程app