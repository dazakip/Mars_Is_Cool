package mic.model;

/**
 * Created by Dazak on 15/03/2017.
 */
public class ImageLocations {

    private String folderPath;
    private String selectedViewPath;

    private String lastFRONTHAZ;
    private String lastREARHAZ;
    private String lastLEFTHAZ;
    private String lastRIGHTHAZ;
    private String lastMAST;
    private String lastMARDI;
    private String lastMAHLI;
    private String lastCHEM;

    public ImageLocations () {
        folderPath = "C:\\Users\\Dazak\\Desktop\\mic_imgs\\";
        selectedViewPath = folderPath+"LEFTHAZ\\";

        lastFRONTHAZ = folderPath +"FRONTHAZ\\0";
        lastREARHAZ = folderPath +"REARHAZ\\0";
        lastRIGHTHAZ = folderPath +"RIGHTHAZ\\0";
        lastLEFTHAZ = folderPath +"LEFTHAZ\\0";
        lastMAHLI = folderPath +"MAHLI\\0";
        lastMAST = folderPath +"MAST\\0";
        lastMARDI = folderPath +"MARDI\\0";
        lastCHEM = folderPath +"CHEM\\0";
    }

    public String getCamera(String camera) {
        return folderPath+camera+"\\";
    }

    public String getSelectedView(){
        return selectedViewPath;
    }

    public void setSelectedView(String newCamera) {
        selectedViewPath = folderPath+newCamera+"\\";
    }

    public void setLastAvailable(String camera, int sol) {
        if (camera.equals("FRONTHAZ"))
            lastFRONTHAZ = folderPath+camera+"\\"+sol;
        else if (camera.equals("REARHAZ"))
            lastREARHAZ = folderPath+camera+"\\"+sol;
        else if (camera.equals("LEFTHAZ"))
            lastLEFTHAZ = folderPath+camera+"\\"+sol;
        else if (camera.equals("RIGHTHAZ"))
            lastRIGHTHAZ = folderPath+camera+"\\"+sol;
        else if (camera.equals("MAHLI"))
            lastMAHLI = folderPath+camera+"\\"+sol;
        else if (camera.equals("MAST"))
            lastMAST = folderPath+camera+"\\"+sol;
        else if (camera.equals("MARDI"))
            lastMARDI = folderPath+camera+"\\"+sol;
        else if (camera.equals("CHEM"))
            lastCHEM = folderPath+camera+"\\"+sol;
    }

    public String getLastAvailable(String camera) {
        if (camera.equals("FRONTHAZ"))
            return lastFRONTHAZ;
        else if (camera.equals("REARHAZ"))
            return lastREARHAZ;
        else if (camera.equals("LEFTHAZ"))
            return lastLEFTHAZ;
        else if (camera.equals("RIGHTHAZ"))
            return lastRIGHTHAZ;
        else if (camera.equals("MAHLI"))
            return lastMAHLI;
        else if (camera.equals("MAST"))
            return lastMAST;
        else if (camera.equals("MARDI"))
            return lastMARDI;
        else if (camera.equals("CHEM"))
            return lastCHEM;

        return null;
    }

    public String getCamLabel(String camera) {
        if (camera.equals("FRONTHAZ"))
            return "Front Hazard Camera";
        else if (camera.equals("REARHAZ"))
            return "Rear Hazard Camera";
        else if (camera.equals("LEFTHAZ"))
            return "Left Hazard Camera";
        else if (camera.equals("RIGHTHAZ"))
            return "Right Hazard Camera";
        else if (camera.equals("MAHLI"))
            return "Mars Hand Lens Imager";
        else if (camera.equals("MAST"))
            return "Mast Camera";
        else if (camera.equals("MARDI"))
            return "Mars Descent Imager";
        else if (camera.equals("CHEM"))
            return "Chemistry Camera";
        else if (camera.equals("CURATED"))
            return "Curated View";

        return null;
    }
}
