public class Response {
    Entry[] results;

    public static class Entry {
        UniProtKBCrossReference[] uniProtKBCrossReferences;

        Sequence sequence;
        public static class UniProtKBCrossReference {
            String database;

            Property[] properties;
            public static class Property {
                String key;
                String value;
            }

        }
        public static class Sequence {
            String value;
        }
    }

}