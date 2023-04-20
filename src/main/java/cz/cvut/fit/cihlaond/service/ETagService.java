package cz.cvut.fit.cihlaond.service;

public class ETagService {
    public static String etagFromVersion(Long version) {
        return "\"" + version + "\"";
    }

}
