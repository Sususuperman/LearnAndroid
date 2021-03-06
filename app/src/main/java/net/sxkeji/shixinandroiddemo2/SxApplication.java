package net.sxkeji.shixinandroiddemo2;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.umeng.analytics.MobclickAgent;

import net.sxkeji.shixinandroiddemo2.hybrid.handler.UIHandler;
import net.sxkeji.shixinandroiddemo2.hybrid.handler.HybridFactory;
import net.sxkeji.shixinandroiddemo2.weex.adapter.DefaultWebSocketAdapterFactory;
import net.sxkeji.shixinandroiddemo2.weex.adapter.ImageAdapter;
import net.sxkeji.shixinandroiddemo2.weex.adapter.PlayDebugAdapter;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import top.shixinzhang.sxframework.BaseApplication;
import top.shixinzhang.sxframework.statistic.CrashHandler;
import top.shixinzhang.utils.ApplicationUtils;

/**
 * <br/> Description:
 * 初始化操作能放到线程的放到线程，最好使用 RxJava 分到多个线程，然后组合
 * <p>
 * <br/> Created by shixinzhang on 16/12/21.
 * <p>
 * <br/> Email: shixinzhang2016@gmail.com
 * <p>
 * <a  href="https://about.me/shixinzhang">About me</a>
 */

public class SxApplication extends BaseApplication {
    private final String TAG = this.getClass().getSimpleName();

    private static SxApplication mApplication;

    public static SxApplication getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;

        if (BuildConfig.DEBUG) {
//            enableStrictMode();
            enableStetho();
        }

        registerHybridHandler();

        CrashHandler.init(this);

//        initANRWatch();

//        initRealm();

        initWeex();

        initUmengAnalytics();
    }

    /**
     * 友盟统计
     */
    private void initUmengAnalytics() {
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 使用 Stetho 进行 Chrome 调试
     */
    private void enableStetho() {
//        https://github.com/facebook/stetho
//        Stetho.initializeWithDefaults(this);
    }

    /**
     * 开启严格模式
     */
    private void enableStrictMode() {
        //ALL_THREAD_DETECT_BITS = DETECT_DISK_WRITE | DETECT_DISK_READ | DETECT_NETWORK | DETECT_CUSTOM | DETECT_RESOURCE_MISMATCH;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()   //在 adb logcat 中显示异常日志
                .build());

        //DETECT_VM_ACTIVITY_LEAKS | DETECT_VM_CURSOR_LEAKS| DETECT_VM_CLOSABLE_LEAKS | DETECT_VM_REGISTRATION_LEAKS| DETECT_VM_FILE_URI_EXPOSURE;
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()    //检查 Activity 、游标、文件、数据库等泄漏
                .penaltyLog()
                .penaltyDeath() //直接奔溃
                .build());
    }

    private void initANRWatch() {
        new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                System.out.println(error.getMessage() + ", " + System.currentTimeMillis());
                String foregroundAppName = ApplicationUtils.getForegroundAppName(getBaseContext());
                System.out.println(foregroundAppName);
                ApplicationUtils.startApplication(getBaseContext(), "com.tencent.mm");
                System.exit(0);
            }
        }).start();
    }

    private void initWeex() {
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new ImageAdapter(this))
                .setDebugAdapter(new PlayDebugAdapter())
                .setWebSocketAdapterFactory(new DefaultWebSocketAdapterFactory())
                .build();
        WXSDKEngine.initialize(this, config);
    }

    private void initRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder()
                        .name("shixinzhang.realm")
                        .deleteRealmIfMigrationNeeded()
//                        .inMemory() //数据保存在内存
                        .build()
        );
    }

    /**
     * 注册 Hybrid 工厂
     */
    private void registerHybridHandler() {
        HybridFactory.getInstance().registerHandlers(UIHandler.class);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

    }

    /**
     * 初始化通用布局，比如 ActionBar 之类的，能干类似以前在 BaseActivity 干的事
     *
     * @param activity
     * @param savedInstanceState
     */
    private void initCommonView(Activity activity, Bundle savedInstanceState) {

    }
}
