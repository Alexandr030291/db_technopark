package org.ebitbucket.model;

public  class Rps {
   private static long rps=0;

    public static long getRps() {
        return rps;
    }

    public static void setRps() {
       rps++;
    }
}
