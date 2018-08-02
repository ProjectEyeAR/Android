package org.team.eye;

public class Session {
    private static Session mInstance;
    private boolean mHaveSession;

    public static Session getInstance() {
        if (mInstance == null)  {
            mInstance = new Session();
        }

        return mInstance;
    }

    public boolean isHaveSession() {
        return mHaveSession;
    }

    public void setHaveSession(boolean haveSession) {
        this.mHaveSession = haveSession;
    }
}
