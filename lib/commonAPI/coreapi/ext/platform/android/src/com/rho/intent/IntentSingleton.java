package com.rho.intent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.rhomobile.rhodes.Logger;
import com.rhomobile.rhodes.RhodesActivity;
import com.rhomobile.rhodes.RhodesService;
import com.rhomobile.rhodes.api.IMethodResult;
import com.rhomobile.rhodes.extmanager.AbstractRhoListener;
import com.rhomobile.rhodes.extmanager.IRhoExtManager;
import com.rhomobile.rhodes.extmanager.IRhoListener;
import com.rhomobile.rhodes.util.ContextFactory;

public class IntentSingleton extends AbstractRhoListener implements IIntentSingleton, IIntentFactory, IRhoListener {
    private static final String TAG = IntentSingleton.class.getSimpleName();
    
    private IMethodResult methodResult;
    
    private int lastRequest = 0;
    private List<Map.Entry<Integer, IMethodResult>> localMethodResults = new ArrayList<Map.Entry<Integer, IMethodResult>>();

    private Intent makeIntent(Map<String, Object> params) {
        Intent intent = new Intent();
        
        Object actionObj = params.get(HK_ACTION);
        Object categoriesObj = params.get(HK_CATEGORIES);
        Object appNameObj = params.get(HK_APP_NAME);
        Object targetClassObj = params.get(HK_TARGET_CLASS);
        Object uriObj = params.get(HK_URI);
        Object mimeObj = params.get(HK_MIME_TYPE);
        Object extrasObj = params.get(HK_DATA);

        String action = null;
        List<String> categories = null;
        String appName = null;
        String targetClass = null;
        String uri = null;
        String mime = null;
        Map<String, Object> extras = null;

        //--- Check param types ---

        if (actionObj != null) {
            if (!String.class.isInstance(actionObj)) {
                throw new RuntimeException("Wrong intent action: " + actionObj.toString());
            }
            action = (String)actionObj;
        }

        if (categoriesObj != null) {
            if (!List.class.isInstance(categoriesObj)) {
                throw new RuntimeException("Wrong intent categories: " + categoriesObj.toString());
            }
            categories = (List<String>)categoriesObj;
        }

        if (appNameObj != null) {
            if (!String.class.isInstance(appNameObj)) {
                throw new RuntimeException("Wrong intent appName: " + appNameObj.toString());
            }
            appName = (String)appNameObj;
        }

        if (targetClassObj != null) {
            if (!String.class.isInstance(targetClassObj)) {
                throw new RuntimeException("Wrong intent targetClass: " + targetClassObj.toString());
            }
            targetClass = (String)targetClassObj;
        }

        if (uriObj != null) {
            if (!String.class.isInstance(uriObj)) {
                throw new RuntimeException("Wrong intent uri: " + uriObj.toString());
            }
            uri = (String)uriObj;
        }

        if (mimeObj != null) {
            if (!String.class.isInstance(mimeObj)) {
                throw new RuntimeException("Wrong intent mimeType: " + mimeObj.toString());
            }
            mime = (String)mimeObj;
        }

        if (extrasObj != null) {
            if (!Map.class.isInstance(extrasObj)) {
                throw new RuntimeException("Wrong intent data: " + extrasObj.toString());
            }
            extras = (Map<String, Object>)extrasObj;
        }
        
        //--- Fill intent fields ---
        
        if (action != null) { 
            intent.setAction((String)action);
        }

        if (categories != null) {
            for(String category : categories) {
                intent.addCategory((String)category);
            }
        }
        
        if (targetClass != null) {
            if (appName == null) {
                throw new RuntimeException("Wrong intent appName: cannot be nil if targetClass is set");
            }
            intent.setClassName((String)appName, (String)targetClass);
        }
        else if (appName != null){
            intent.setPackage((String)appName);
        }

        if (uri != null) {
            Uri data = Uri.parse((String)uri);
            if (mime == null) {
                intent.setData(data);
            }
            else {
                intent.setDataAndTypeAndNormalize(data, mime);
            }
        }
        else if (mime != null) {
            intent.setTypeAndNormalize((String)mime);
        }
        
        if (extras != null) {
            for (Map.Entry<String, Object> entry: extras.entrySet()) {
                if (String.class.isInstance(entry.getValue())) {
                    intent.putExtra(entry.getKey(), (String)entry.getValue());
                }
                else if (Boolean.class.isInstance(entry.getValue())) {
                    intent.putExtra(entry.getKey(), ((Boolean)entry.getValue()).booleanValue());
                }
                else if (Integer.class.isInstance(entry.getValue())) {
                    intent.putExtra(entry.getKey(), ((Integer)entry.getValue()).intValue());
                }
                else if (Double.class.isInstance(entry.getValue())) {
                    intent.putExtra(entry.getKey(), ((Double)entry.getValue()).doubleValue());
                }
                else {
                    throw new RuntimeException("Wrong intent data: " + entry.getValue().getClass().getName() + " is not supported as value");
                }
            }
        }
        
        return intent;
    }
    
    private Map<String, Object> parseIntent(Intent intent) {
        Map<String, Object> params = new HashMap<String, Object>();
        
        String action = intent.getAction();
        if (action != null) {
            params.put(HK_ACTION, action);
        }
        
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            params.put(HK_CATEGORIES, categories);
        }
        
        String appName = intent.getPackage();
        if (appName != null) {
            params.put(HK_APP_NAME, appName);
        }
        
        String uri = intent.getDataString();
        if (uri != null) {
            uri = Uri.decode(uri);
            params.put(HK_URI, uri);
        }
        
        String mime = intent.getType();
        if (mime != null) {
            params.put(HK_MIME_TYPE, mime);
        }
        
        Bundle extras = intent.getExtras();
        if (extras != null) {
            params.put(HK_DATA, extras);
        }
        
        return params;
    }
    
    @Override
    public void send(Map<String, Object> params, IMethodResult result) {
        Intent intent = makeIntent(params);
        Object type = params.get(HK_INTENT_TYPE);
        if (type.equals(BROADCAST)) {
            Object permissionObj = params.get(HK_PERMISSION);
            String permission = null;
            if (permissionObj != null) {
                if (String.class.isInstance(permissionObj)) {
                    permission = (String)permissionObj;
                }
                else {
                    result.setArgError("Wrong intent permission: " + permissionObj);
                    return;
                }
            }
            ContextFactory.getContext().sendBroadcast(intent, permission);
        }
        else if (type.equals(START_ACTIVITY)) {
            if (result.hasCallback()) {
                int request;
                synchronized (localMethodResults) {
                    request = lastRequest;
                    Map.Entry<Integer, IMethodResult> entry = new AbstractMap.SimpleEntry<Integer, IMethodResult>(Integer.valueOf(request), result);
                    localMethodResults.add(entry);
                    ++lastRequest;
                }
                RhodesActivity.safeGetInstance().startActivityForResult(intent, request);
            }
            else {
                ContextFactory.getUiContext().startActivity(intent);
            }
        }
        else if (type.equals(START_SERVICE)) {
            ContextFactory.getContext().startService(intent);
        }
        else {
            result.setArgError("Wrong intent type: " + type.toString());
        }
    }

    @Override
    synchronized public void startListening(IMethodResult result) {
        methodResult = result;
    }

    @Override
    synchronized public void stopListening(IMethodResult result) {
        if (methodResult != null) {
            methodResult.release();
            methodResult = null;
        }
    }

    @Override
    public IIntentSingleton getApiSingleton() {
        return this;
    }

    @Override
    public IIntent getApiObject(String id) {
        return null;
    }

    @Override
    public void onCreateApplication(IRhoExtManager extManager) {
        IntentFactorySingleton.setInstance(this);
        extManager.addRhoListener(this);
    }

    @Override
    public void onNewIntent(RhodesActivity activity, Intent intent) {
        onNewIntent(START_ACTIVITY, intent);
    }

    @Override
    public void onNewIntent(RhodesService activity, Intent intent) {
        onNewIntent(START_SERVICE, intent);
    }
    
    synchronized public void onNewIntent(String type, Intent intent) {
        if (methodResult != null) {
            Logger.T(TAG, "New Intent: " + type);
            Map<String, Object> params = parseIntent(intent);
            params.put(HK_INTENT_TYPE, type);
            methodResult.set(params);
        }
    }
    
    @Override
    public void onActivityResult(RhodesActivity activity, int requestCode, int resCode, Intent intent) {
        for (Map.Entry<Integer, IMethodResult> resultEntry : localMethodResults) {
            if(resultEntry.getKey().intValue() == requestCode) {
                Logger.T(TAG, "Activity result request: " + requestCode);
                Map<String, Object> params = parseIntent(intent);
                params.put(HK_INTENT_TYPE, START_ACTIVITY);
                params.put(HK_RESPONSE_CODE, Integer.valueOf(resCode));
                methodResult.set(params);
            }
        }
    }


}