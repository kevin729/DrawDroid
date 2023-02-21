package com.professorperson.drawdroid;

/**
 * Created by kevin on 22/02/19.
 */

public interface EditorI {
    interface Model {
        String getCode(int index);
    }

    interface  View {
        void setCode(String code);
    }

    interface Presenter {
        void handleGesture (double[] pixels);
    }
}
