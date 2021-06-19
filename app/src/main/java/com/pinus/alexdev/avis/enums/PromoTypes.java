package com.pinus.alexdev.avis.enums;

import android.content.Context;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.App;

public enum PromoTypes {
    OVERALL(R.string.overalRatingTitle), GENERAL(R.string.generalQrCompanyName), CLEANNESS(R.string.cleannessQrCompanyName), SERVICE(R.string.serviceQrCompanyName);

    private int value;

    public int getValue() {
        return value;
    }
    PromoTypes(int value) {
        this.value = value;
    }

    //НЕ ИСПОЛЬЗОВАТЬ ЭТОТ МЕТОД!!! при использовании мультиязычности нельзя использовать контекст всего приложения ( App.getContext() ),
    // потому что при переводе(при перезапуске) оно в памяти оставляет ресурсы прошлого перевода.
    //Этот метод(fromString(String text)) используется в классе HomeActivity и получает строковые ресурсы с помощью контекста активити.
    // Только при использовании контекста активити всё работает как нужно.
    //Специально оставил этот метод с комментарием, чтобы в дальнейшем тот, кто будет читать этот код, знал об этом нюансе ;)
    //НЕ ИСПОЛЬЗОВАТЬ ЭТОТ МЕТОД!!!
//    public static PromoTypes fromString(String text) {
//        for (PromoTypes b : PromoTypes.values()) {
//            String promoType = App.getContext().getString(b.getValue());
//            if (promoType.equalsIgnoreCase(text)) {
//                return b;
//            }
//        }
//        return null;
//    }
}
