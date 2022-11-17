import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MerMaker {

    final static GUI gui = new GUI();
    static final int FLANK_LENGTH = 5;
    static final int MAX_MER_LENGTH = 11;

    public static void runMe(){
        gui.runMe();
    }

    public static void neo() throws IOException {
        gui.clearWarningLabel();
        var userInput = gui.geneInput.getText().toUpperCase().trim();

        Input inputValue = getInput(userInput);
        var proteinName = inputValue.protein();
        var aminoAcid = inputValue.aminoAcid();
        var aminoAcidPosition = inputValue.aminoAcidPosition();
        var mutation = inputValue.mutation();
        int tpm = inputValue.tpm();

        Protein proteinValue = Protein.getProtein(proteinName);
        var proteinSequence = proteinValue.sequence();
        String geneID = geneIDSelector(proteinValue);
        String mutatedSequence = getMutatedSequence(proteinSequence, aminoAcid, aminoAcidPosition, mutation);

        gui.output.setText(String.join(
                "\n",
                neoStringBuilder(userInput, geneID, mutatedSequence, aminoAcidPosition, tpm)
        ));
    }

    public static void cta() throws IOException {
        gui.clearWarningLabel();

        var input = gui.geneInput.getText().toUpperCase().trim();
        int tpm = Integer.parseInt(gui.tpmInput.getText().trim());
        Protein proteinValue = Protein.getProtein(input);
        String sequence = proteinValue.sequence();
        var z = "ZZZZZ";
        var flankedSequence = z + sequence + z;
        String geneID = geneIDSelector(proteinValue);

        List<String> outputResult = new ArrayList<>();

        outputResult.addAll(ctaStringBuilder(input, geneID,flankedSequence,tpm,11));
        outputResult.addAll(ctaStringBuilder(input, geneID,flankedSequence,tpm,10));
        outputResult.addAll(ctaStringBuilder(input, geneID,flankedSequence,tpm,9));
        outputResult.addAll(ctaStringBuilder(input, geneID,flankedSequence,tpm,8));
        gui.output.setText(String.join("\n", outputResult));
    }

    private static Input getInput(String userInput) {
        int underscoreIndex = userInput.indexOf('_');

        if (underscoreIndex == -1) {
            throw new IllegalArgumentException("input requires \"_\"");
        }

        var protein = userInput.substring(0, underscoreIndex);
        var aminoAcid = userInput.charAt(underscoreIndex + 1);

        if (!Character.isLetter(aminoAcid)) {
            throw new IllegalArgumentException("Expected an amino acid after underscore");
        }

        var aminoAcidPositionStr = userInput.substring(underscoreIndex + 2, userInput.length() - 1);

        if (!aminoAcidPositionStr.matches("^\\d+$")) {
            throw new IllegalArgumentException("Expected an amino acid position");
        }

        var aminoAcidPosition = Integer.parseInt(aminoAcidPositionStr);

        var mutation = userInput.charAt(userInput.length() - 1);

        if (!Character.isLetter(mutation)) {
            throw new IllegalArgumentException("Expected an amino acid in last position");
        }

        var tpmStr = gui.tpmInput.getText();

        if (!tpmStr.matches("^\\d+$")) {
            throw new IllegalArgumentException("Expected a number");
        }

        int tpm = Integer.parseInt(tpmStr);

        return new Input(protein, aminoAcid, aminoAcidPosition, mutation, tpm);
    }

    private static String getMutatedSequence(String proteinSequence,char aminoAcid, int aminoAcidPosition, char mutation) {
        var z = "ZZZZZ";
        var origAminoAcid = proteinSequence.charAt(aminoAcidPosition - 1);
        if (origAminoAcid != aminoAcid) {
            throw new IllegalArgumentException(String.format(
                    "Amino acid at position %d is %c not %c",
                    aminoAcidPosition,
                    origAminoAcid,
                    aminoAcid
            ));
        }
        if (Math.abs(aminoAcidPosition - proteinSequence.length()) < MAX_MER_LENGTH + FLANK_LENGTH) {
            proteinSequence = proteinSequence + z;
        } else if (aminoAcidPosition < MAX_MER_LENGTH + FLANK_LENGTH) {
            proteinSequence = z + proteinSequence;
            aminoAcidPosition += 15;
        }
        return proteinSequence.substring(
                0,
                aminoAcidPosition - 1) + mutation + proteinSequence.substring(aminoAcidPosition
        );
    }

    private static void initGeneIdList(Protein protein) {
        if(!gui.geneIDList.isVisible()) {
            for(var id : protein.geneIds()) {
                gui.geneIDList.addItem(id);
            }

            gui.geneIDList.setVisible(true);
        }
    }

    private static String geneIDSelector(Protein proteinValue) {
        String geneID;
        if(proteinValue.geneIds().size() == 1) {
            geneID = proteinValue.geneIds().get(0);
        } else if(proteinValue.geneIds().size() > 1) {
            initGeneIdList(proteinValue);
            geneID = (String)gui.geneIDList.getSelectedItem();
            var geneCount = proteinValue.geneIds().size();
            gui.warningLabel.setText(geneCount + " gene ID's exist");
        } else {
            geneID = "";
            gui.warningLabel.setText("gene ID value not available");
        }

        return geneID;
    }
    private static String getCTerm(String mutatedSequence, int aminoAcidPosition, int offset){
        return mutatedSequence.substring(aminoAcidPosition - 1 - offset - FLANK_LENGTH, aminoAcidPosition - 1 - offset);
    }

    private static String getNTerm(String mutatedSequence, int aminoAcidPosition, int offset, int mer_length){
        return mutatedSequence.substring(
                (aminoAcidPosition - 1 - offset) + mer_length,
                (aminoAcidPosition -1 - offset) + mer_length + FLANK_LENGTH);
    }

    private static String getMer(String mutatedSequence, int aminoAcidPosition, int offset, int mer_Length){
        return mutatedSequence.substring(
                aminoAcidPosition -1 - offset,
                aminoAcidPosition - 1 -offset + mer_Length);
    }

    private static List<String> ctaStringBuilder(String input, String geneID, String flankedSequence, int tpm, int mer_length){
        List<String> output = new ArrayList<>();

        for (int i = 0; i <= flankedSequence.length() - mer_length - (FLANK_LENGTH * 2); i++) {
            int cTermEnd = i + FLANK_LENGTH;
            int merStart = i + FLANK_LENGTH;
            int merEnd = i + mer_length + FLANK_LENGTH;
            int nTermStart = i + mer_length + FLANK_LENGTH;
            int nTermEnd = i + mer_length + FLANK_LENGTH * 2;
            var cTerm = flankedSequence.substring(i,cTermEnd);
            var mer = flankedSequence.substring(merStart,merEnd);
            var nTerm = flankedSequence.substring(nTermStart,nTermEnd);

            output.add(String.join(",", input, geneID, cTerm, mer, nTerm, String.valueOf(tpm))); //append one item to the list
        }
        return output;
    }

    private static List<String> neoStringBuilder(
            String input,
            String geneID,
            String mutatedSequence,
            int aminoAcidPosition,
            int tpm
    ){
        List<String> output = new ArrayList<>();
        for (int mer_length = 11; mer_length >= 8; mer_length--) {
            for (int i = mer_length - 1; i >= 0; i--) {
                var cTerm = getCTerm(mutatedSequence, aminoAcidPosition, i);
                var mer = getMer(mutatedSequence, aminoAcidPosition, i, mer_length);
                var nTerm = getNTerm(mutatedSequence, aminoAcidPosition, i, mer_length);
                output.add(String.join(",",
                        input, geneID,cTerm,mer,nTerm,Integer.toString(tpm)));
            }
        }
        return output;
    }
}

