package mic.model;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUNIT_ImageLocations {
    ImageLocations imgLoc;
    String folderPath;


    public void setUp(  ) throws SQLException, ClassNotFoundException {
        imgLoc = new ImageLocations();
        folderPath = "C:\\Users\\Dazak\\Desktop\\mic_imgs\\";
    }

    public void tearDown(  ) {
        imgLoc = null;
        folderPath = null;
    }


    @Test
    public void cameraPaths() throws SQLException, ClassNotFoundException {
        setUp();
        assertEquals(folderPath+"LEFTHAZ\\",imgLoc.getCamera("LEFTHAZ"));
        assertEquals(folderPath+"RIGHTHAZ\\",imgLoc.getCamera("RIGHTHAZ"));
        assertEquals(folderPath+"REARHAZ\\",imgLoc.getCamera("REARHAZ"));
        assertEquals(folderPath+"FRONTHAZ\\",imgLoc.getCamera("FRONTHAZ"));
        assertEquals(folderPath+"CHEM\\",imgLoc.getCamera("CHEM"));
        assertEquals(folderPath+"MARDI\\",imgLoc.getCamera("MARDI"));
        assertEquals(folderPath+"MAST\\",imgLoc.getCamera("MAST"));
        assertEquals(folderPath+"MAHLI\\",imgLoc.getCamera("MAHLI"));
        tearDown();
    }

    @Test
    public void selectedView() throws SQLException, ClassNotFoundException {
        setUp();
        imgLoc.setSelectedView("FRONTHAZ");
        assertEquals(folderPath+"FRONTHAZ\\", imgLoc.getSelectedView());

        imgLoc.setSelectedView("CURATED");
        imgLoc.nextSol();
        assertEquals(folderPath+"FRONTHAZ\\", imgLoc.getSelectedView());

        imgLoc.setSelectedView("MADEUPCAMERA");
        assertEquals(folderPath+"FRONTHAZ\\", imgLoc.getSelectedView());

        tearDown();
    }

    @Test
    public void cameraLabels() throws SQLException, ClassNotFoundException {
        setUp();
        assertEquals("Front Hazard Camera", imgLoc.getCamLabel("FRONTHAZ"));
        assertEquals("Rear Hazard Camera", imgLoc.getCamLabel("REARHAZ"));
        assertEquals("Left Hazard Camera", imgLoc.getCamLabel("LEFTHAZ"));
        assertEquals("Right Hazard Camera", imgLoc.getCamLabel("RIGHTHAZ"));
        assertEquals("Mast Camera", imgLoc.getCamLabel("MAST"));
        assertEquals("Mars Descent Imager", imgLoc.getCamLabel("MARDI"));
        assertEquals("Mars Hand Lens Imager", imgLoc.getCamLabel("MAHLI"));
        assertEquals("Chemistry Camera", imgLoc.getCamLabel("CHEM"));
        assertEquals("Curated View", imgLoc.getCamLabel("CURATED"));
        assertEquals(null, imgLoc.getCamLabel("RANDOM CAM"));
        tearDown();
    }

    @Test
    public void curatedPath() throws SQLException, ClassNotFoundException {
        setUp();
        assertEquals(folderPath+"FRONTHAZ_th\\",imgLoc.getCurated());
        imgLoc.nextSol();
        imgLoc.nextSol();
        imgLoc.nextSol();
        assertEquals(folderPath+"MAST_th\\",imgLoc.getCurated());
        imgLoc.nextSol();
        assertEquals(folderPath+"FRONTHAZ_th\\",imgLoc.getCurated());
        imgLoc.nextSol();
        imgLoc.nextSol();
        imgLoc.nextSol();
        imgLoc.nextSol();
        imgLoc.nextSol();
        imgLoc.nextSol();
        assertEquals(folderPath+"LEFTHAZ_th\\",imgLoc.getCurated());
        tearDown();
    }
}