package id.firdausns.test_firdaus.Config;

public class ItemRecord {
    public String uri, filename;

    public ItemRecord(String uri, String filename){
        this.uri = uri;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getUri() {
        return uri;
    }
}
