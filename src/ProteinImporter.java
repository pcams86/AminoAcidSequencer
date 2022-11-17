import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ProteinImporter {
    public HashMap<String, String> map = new HashMap<>();

    public ProteinImporter() throws IOException {
        String line;
        var uniprotID = "";
        var geneName = "";
        var proteinSequence = "";


        FileReader FILE = new FileReader("Input/protein_list.txt");
        BufferedReader reader = new BufferedReader(FILE);
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(">")) {
                if (!proteinSequence.isEmpty()){
                    map.put(uniprotID,proteinSequence);
                    map.put(geneName, proteinSequence);
                    proteinSequence = "";
                }
                var idNames = line.substring(4,line.indexOf('_'));
                uniprotID = idNames.substring(0,idNames.indexOf('|'));
                geneName = idNames.substring(idNames.indexOf('|')+1);

            }
            else {
                    proteinSequence += line.trim();
            }
        }
        if (!proteinSequence.isEmpty()){
            map.put(uniprotID,proteinSequence);
            map.put(geneName, proteinSequence);
        }
    }
}
