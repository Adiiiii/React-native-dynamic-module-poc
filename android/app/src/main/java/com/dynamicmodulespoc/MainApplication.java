package com.dynamicmodulespoc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    SplitInstallManager splitInstallManager;
    private int mySessionId;

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // packages.add(new MyReactNativePackage());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());

      splitInstallManager = SplitInstallManagerFactory.create(this);
      downloadFeatureModule();
  }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SplitCompat.install(this);
    }

    private void downloadFeatureModule() {
      Log.d("BobTheBuilder", "inside function downloadFeatureModule");
        SplitInstallRequest request =  SplitInstallRequest.newBuilder().addModule("dynamicfeature").build();
        SplitInstallStateUpdatedListener listener = splitInstallSessionState -> {
            if(splitInstallSessionState.sessionId() == mySessionId) {
                switch (splitInstallSessionState.status()) {

                    case SplitInstallSessionStatus.DOWNLOADING:
                        Log.d("BobTheBuilder", " Dynamic feature download started");
                        break;

                    case SplitInstallSessionStatus.DOWNLOADED:
                        Log.d("BobTheBuilder", " Dynamic feature download complete");
                        break;

                    case SplitInstallSessionStatus.INSTALLED:
                        Log.d("BobTheBuilder", " Dynamic feature Installation complete");
                        Intent intent = new Intent();
                        intent.setClassName(BuildConfig.APPLICATION_ID, "com.simple.dynamicfeature.MainActivity");
                        startActivity(intent);
                        break;

                    case SplitInstallSessionStatus.CANCELED:
                        Log.d("BobTheBuilder", " Dynamic feature download CANCELED");
                        break;
                    case SplitInstallSessionStatus.CANCELING:
                        Log.d("BobTheBuilder", " Dynamic feature download CANCELING");
                        break;
                    case SplitInstallSessionStatus.FAILED:
                        Log.d("BobTheBuilder", " Dynamic feature download FAILED");
                        break;
                    case SplitInstallSessionStatus.INSTALLING:
                        Log.d("BobTheBuilder", " Dynamic feature download INSTALLING");
                        break;
                    case SplitInstallSessionStatus.PENDING:
                        Log.d("BobTheBuilder", " Dynamic feature download PENDING");
                        break;
                    case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                        Log.d("BobTheBuilder", " Dynamic feature download REQUIRES_USER_CONFIRMATION");
                        break;
                    case SplitInstallSessionStatus.UNKNOWN:
                        Log.d("BobTheBuilder", " Dynamic feature download UNKNOWN");
                        break;
                }
            }
        };
        splitInstallManager.registerListener(listener);
        splitInstallManager.startInstall(request)
                .addOnFailureListener(e ->
                        Log.d("BobTheBuilder", " Dynamic feature download initialization", e)
                )
                .addOnSuccessListener(sid -> mySessionId = sid);
        Log.d("BobTheBuilder", "Dynamic feature download initialization");
    }

  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.dynamicmodulespoc.ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
