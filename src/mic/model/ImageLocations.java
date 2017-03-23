package mic.model;

import java.sql.*;

/**
 * Created by Dazak on 15/03/2017.
 */
public class ImageLocations {

    private String folderPath;
    private String selectedViewPath;

    private Connection c;
    private Statement stmt;
    private ResultSet rs;

    private int sol;

    public ImageLocations () throws SQLException, ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:resources/CuratedView.db");
        c.setAutoCommit(false);
        sol = 0;
        folderPath = "resources/mic_imgs/";
        setSelectedView("CURATED");
    }

    public void nextSol(){
        sol = sol + 1;
    }

    public String getCamera(String camera)  {
        return folderPath+camera+"/";
    }

    public String getCurated() throws SQLException{
        stmt = c.createStatement();
        rs = stmt.executeQuery( "SELECT * FROM CURATED WHERE SOL ="+sol+";");
        rs.next();
        return folderPath+rs.getString("CAMERA")+"_th/";
    }

    public String getSelectedView() throws SQLException {
        if (selectedViewPath.equals("CURATED")) {
            stmt = c.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM CURATED WHERE SOL ="+sol+";");
            while (rs.next()) {
                return folderPath+rs.getString("CAMERA")+"/";
            }
        }
        return selectedViewPath;
    }

    public void setSelectedView(String newCamera) {
        if (newCamera.equals("CURATED")) {
            selectedViewPath = "CURATED";
        }
        else if (checkValidCamera(newCamera))
            selectedViewPath = folderPath+newCamera+"/";

    }

    private boolean checkValidCamera(String cam){
        if (cam.equals("FRONTHAZ")||cam.equals("REARHAZ")||cam.equals("LEFTHAZ")||cam.equals("RIGHTHAZ")||
                cam.equals("MAST")||cam.equals("MARDI")||cam.equals("MAHLI")||cam.equals("CHEM")) {
                    return true;
        }
        else
            return false;
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
