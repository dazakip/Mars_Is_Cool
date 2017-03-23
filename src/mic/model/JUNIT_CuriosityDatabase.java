package mic.model;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Dazak on 21/03/2017.
 */
public class JUNIT_CuriosityDatabase {

    CuriosityDatabase db;
    DataEntry d;

    public void setUp(  ) throws SQLException, ClassNotFoundException {
        db = new CuriosityDatabase();
        db.nextSol(9);
        d = new DataEntry();
        d.setTimeStamp(398297265);
        d.setSol(9);
        d.setTime("14:35:12");
        d.setElevation(0.0);
        d.setAirTemp(256.48);
        d.setUVA(11111);
        d.setPressure(2.7);
        d.setHumidity(11111);
    }

    public void tearDown(  ) {
        db = null;
        d = null;
    }


    @Test
    public void dataEntries() throws SQLException, ClassNotFoundException {
        setUp();
        assertEquals(10, db.getDataEntries().size());
        DataEntry newD = db.getDataEntries().get(0);
        assertEquals(398297265-397445868, newD.getTimeStamp());
        assertEquals(9, newD.getSol());
        assertEquals("14:35:12", newD.getTime());
        assertEquals(0.0, newD.getElevation());
        assertEquals(256.48-273.15, newD.getAirTemp());
        assertEquals(11111, newD.getUVA());
        assertEquals(2.7, newD.getPressure());
        assertEquals(11111, newD.getHumidity());
        tearDown();
    }

    @Test
    public void updateEntrys() throws SQLException, ClassNotFoundException {
        setUp();
        for (int i=1;i<100;i++) {
            db.nextSol(i);
            db.updateEntries((i * 88765) - (88765*10),(i * 88765));
        }
        tearDown();
    }
}
