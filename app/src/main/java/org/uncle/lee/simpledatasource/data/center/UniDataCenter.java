package org.uncle.lee.simpledatasource.data.center;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.uncle.lee.simpledatasource.Entity.in.App;
import org.uncle.lee.simpledatasource.Entity.in.Contact;
import org.uncle.lee.simpledatasource.Utils.Transformer;
import org.uncle.lee.simpledatasource.controller.UniDataController;
import org.uncle.lee.simpledatasource.listener.DataControllerListener;
import org.uncle.lee.simpledatasource.listener.UniDataCenterListener;

/**
 * Created by Austin on 2016/7/7.
 */
public class UniDataCenter implements DataCenter {
  private static UniDataCenter uniDataCenter;
  private UniDataCenterListener listener;
  private UniDataController uniDataController;
  private Context mContext;
  private CacheData cacheData;
  private List<Contact> contactTemp;
  private List<App> appTemp;

  private UniDataCenter(Context mContext){
    this.mContext = mContext;
    uniDataController = new UniDataController(mContext);
    cacheData = new CacheData();
  }

  public static UniDataCenter getInstance(Context mContext){
    if(uniDataCenter == null){
      synchronized (UniDataCenter.class){
        if(uniDataCenter == null){
          uniDataCenter = new UniDataCenter(mContext);
        }
      }
    }
    return uniDataCenter;
  }

  @Override public void queryContactList() {
    if(cacheData.cacheContactList() ==  null){
      queryContactInFirstTime();
    }else {
      this.listener.onAction(UniDataCenterListener.ActionType.QUERY_ALL_DONE, true, cacheData.cacheContactList());
    }
  }

  private void queryContactInFirstTime() {
    uniDataController.contactDao().setListener(new DataControllerListener<Contact>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<Contact> contacts) {
        if(Type.equals(ActionType.QUERY_ALL_DONE) && isSuccess){
          UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.QUERY_ALL_DONE, true, contacts);
          saveCacheContactList(contacts);
        }
      }
    });
    uniDataController.contactDao().queryAll();
  }

  @Override public void insertContactList(List<Contact> contactList) {
    this.contactTemp = contactList;
    //this.contactTemp = Transformer.addPyForContact(mContext, contactList);
    uniDataController.contactDao().setListener(new DataControllerListener<Contact>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<Contact> contacts) {
        if(Type.equals(ActionType.INSERT_DONE) && isSuccess){
          UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.INSERT_DONE, true, null);
          saveCacheContactList(contactTemp);
        }
      }
    });
    uniDataController.contactDao().insert(contactList);
  }

  private void saveCacheContactList(List<Contact> contacts) {
    cacheData.saveCacheContactList(contacts);
  }

  @Override public void cleanContactList() {
    uniDataController.contactDao().setListener(new DataControllerListener<Contact>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<Contact> contacts) {
        if(Type.equals(ActionType.CLEAN_DONE) && isSuccess){
          UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.CLEAN_DONE, true, null);
          saveCacheContactList(Collections.<Contact>emptyList());
        }
      }
    });
    uniDataController.contactDao().clean();
  }

  @Override public void queryAppList() {
    if(cacheData.cacheAppList() == null){
      queryAppInFirstTime();
    }else {
      this.listener.onAction(UniDataCenterListener.ActionType.QUERY_ALL_DONE, true, cacheData.cacheAppList());
    }
  }

  private void queryAppInFirstTime() {
    uniDataController.appDao().setListener(new DataControllerListener<App>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<App> apps) {
        if(Type.equals(ActionType.QUERY_ALL_DONE) && isSuccess){
          UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.QUERY_ALL_DONE, true, apps);
          saveCacheAppList(apps);
        }
      }
    });
    uniDataController.appDao().queryAll();
  }

  private void saveCacheAppList(List<App> apps) {
    cacheData.saveCacheAppList(apps);
  }

  @Override public void insertAppList(List<App> appList) {
    // app don't need to get py params(this part is too wasting time)
    this.appTemp = appList;
    uniDataController.appDao().setListener(new DataControllerListener<App>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<App> apps) {
        if(Type.equals(ActionType.INSERT_DONE) && isSuccess){
          UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.INSERT_DONE, true, null);
          saveCacheAppList(appTemp);
        }
      }
    });
    uniDataController.appDao().insert(appList);
  }

  @Override public void cleanAppList() {
    uniDataController.appDao().setListener(new DataControllerListener<App>() {
      @Override public void onAction(ActionType Type, boolean isSuccess, List<App> apps) {
        UniDataCenter.this.listener.onAction(UniDataCenterListener.ActionType.CLEAN_DONE, true, null);
        saveCacheAppList(Collections.<App>emptyList());
      }
    });
    uniDataController.appDao().clean();
  }

  @Override public void setListener(UniDataCenterListener listener) {
    this.listener = listener;
  }
}
