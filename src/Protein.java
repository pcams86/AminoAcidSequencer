import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record Protein(String proteinName, List<String> geneIds, String sequence) {
    public static Protein getProtein(String proteinName) throws IOException {
        URL url = new URL(
                "https://rest.uniprot.org/uniprotkb/search?compressed=false&format=json&query=" + proteinName);

        String json;
        try {
            json = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IOException("No internet Connection");
        }

        var gson = new Gson();
        var response = gson.fromJson(json, Response.class);

        if(response.results.length == 0) {
            throw new IllegalArgumentException("Protein name not recognized");
        }

        var entry = response.results[0];

        List<String> geneIds = new ArrayList<>();
        for(var crossReference : entry.uniProtKBCrossReferences) {
            if(crossReference.database.equals("Ensembl")) {
                for(var property : crossReference.properties) {
                    if(property.key.equals("GeneId")) {
                        var geneId = property.value.split("\\.")[0];
                        if (!geneIds.contains(geneId)) {
                            geneIds.add(geneId);
                        }
                    }
                }
            }
        }
        return new Protein(proteinName, geneIds, entry.sequence.value);
    }
}