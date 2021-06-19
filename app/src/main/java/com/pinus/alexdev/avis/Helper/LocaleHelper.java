package com.pinus.alexdev.avis.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Created by AlexDev on 5/16/2018.
 */

public class LocaleHelper {
    private static final String SELECT_LANGUAGE = "Local.Helper.Selected.Language";

    public static Context onAttach(Context context){
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defautLanguage){
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }


    public static Context setLocale(Context context, String lang){
        persist(context, lang);
       // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        //    return  updateResources(context, lang);
        return updateResourceLegacy(context, lang);
    }

  //  @SuppressWarnings("deprecation")
    private static  Context updateResourceLegacy(Context context, String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    private static void  persist(Context context, String lang){
         SharedPreferences pref  = context.getSharedPreferences(
                 SELECT_LANGUAGE, Context.MODE_PRIVATE);
         SharedPreferences.Editor editor =pref.edit();

        editor.putString(SELECT_LANGUAGE, lang);
        editor.apply();
    }

    private static String getPersistedData(Context context, String language){
        SharedPreferences preferences =  context.getSharedPreferences(
                SELECT_LANGUAGE, Context.MODE_PRIVATE);
            return  preferences.getString(SELECT_LANGUAGE, language);
    }
}
