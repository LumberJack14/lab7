package Utils;

import Collection.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

public class DragonDatabaseManager {
    public Connection conn;

    public DragonDatabaseManager() {
        Properties info = new Properties();
        try {
            info.load(new FileInputStream("db.cfg"));
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Dragons", info);
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public synchronized int addDragon(DragonX dragon) {
        int rowsAffected = 0;
        try {
            String insertString = """
                    INSERT INTO dragon (
                        DRAGON_NAME,
                        DRAGON_AGE,
                        DRAGON_DESC,
                        DRAGON_TYPE,
                        DRAGON_CHARACTER,
                        DRAGON_COORDINATE_X,
                        DRAGON_COORDINATE_Y,
                        KILLER_NAME,
                        KILLER_PASSPORT_ID,
                        KILLER_WEIGHT,
                        KILLER_LOCATION_X,
                        KILLER_LOCATION_Y,
                        KILLER_LOCATION_Z,
                        KILLER_LOCATION_NAME,
                        CREATION_DATE,
                        DRAGON_OWNER
                    ) VALUES (
                        ?, -- text
                        ?, -- bigint
                        ?, -- text
                        ?, -- varchar(20)
                        ?, -- VARCHAR(20),
                        ?, -- DOUBLE PRECISION,
                        ?, --  FLOAT,
                        ?, -- TEXT
                        ?, -- TEXT
                        ?, -- int
                        ?, -- int
                        ?, -- bigint
                        ?, -- float
                        ?, -- text
                        ?, -- timestamp
                        ?  -- text
                    );
                    """;
            PreparedStatement stat = this.conn.prepareStatement(insertString);
            stat.setString(1, dragon.getName());
            stat.setLong(2, dragon.getAge());
            stat.setString(3, dragon.getDescription());
            stat.setString(4, dragon.getType().name());
            stat.setString(5, dragon.getCharacter().name());
            stat.setDouble(6, dragon.getCoordinates().getX());
            stat.setFloat(7, dragon.getCoordinates().getY());
            stat.setString(8, dragon.getKiller().getName());
            stat.setString(9, dragon.getKiller().getPassportID());
            stat.setInt(10, dragon.getKiller().getWeight());
            stat.setInt(11, dragon.getKiller().getLocation().getX());
            stat.setLong(12, dragon.getKiller().getLocation().getY());
            stat.setFloat(13, dragon.getKiller().getLocation().getZ());
            stat.setString(14, dragon.getKiller().getLocation().getName());
            stat.setTimestamp(15, Timestamp.valueOf(dragon.getCreationDate().atStartOfDay()));
            stat.setString(16, dragon.getOwner());
            rowsAffected = stat.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("added new dragon to the database");
            } else System.out.println("failed to add new dragon");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public synchronized int deleteDragon(long id) {
        int rowsAffected = 0;
        try {
            String deleteString = """
                    DELETE FROM Dragon
                    WHERE ID = ?;
                    """;
            PreparedStatement stat = this.conn.prepareStatement(deleteString);
            stat.setLong(1, id);
            rowsAffected = stat.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("deleted the dragon from the database");
            } else System.out.println("failed to delete the dragon");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return rowsAffected;
    }

    public Vector<DragonX> returnCollection() {
        Vector<DragonX> collection = new Vector<>();
        try {
            String selectString = """
                    select * from dragon;
                    """;
            PreparedStatement stat = this.conn.prepareStatement(selectString);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                DragonX dragon = new DragonX();
                dragon.setId(rs.getLong("ID"));
                dragon.setName(rs.getString("DRAGON_NAME"));
                dragon.setAge(rs.getLong("DRAGON_AGE"));
                dragon.setDescription(rs.getString("DRAGON_DESC"));
                dragon.setType(DragonTypeX.valueOf(rs.getString("DRAGON_TYPE")));
                dragon.setCharacter(DragonCharacterX.valueOf(rs.getString("DRAGON_CHARACTER")));
                CoordinatesX coordinates = new CoordinatesX();
                coordinates.setX(rs.getDouble("DRAGON_COORDINATE_X"));
                coordinates.setY(rs.getFloat("DRAGON_COORDINATE_Y"));
                dragon.setCoordinates(coordinates);
                PersonX killer = new PersonX();
                killer.setName(rs.getString("KILLER_NAME"));
                killer.setPassportID(rs.getString("KILLER_PASSPORT_ID"));
                killer.setWeight(rs.getInt("KILLER_WEIGHT"));
                LocationX location = new LocationX();
                location.setX(rs.getInt("KILLER_LOCATION_X"));
                location.setY(rs.getLong("KILLER_LOCATION_Y"));
                location.setZ(rs.getFloat("KILLER_LOCATION_Z"));
                location.setName(rs.getString("KILLER_LOCATION_NAME"));
                killer.setLocation(location);
                dragon.setKiller(killer);
                dragon.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime().toLocalDate());
                dragon.setOwner(rs.getString("DRAGON_OWNER"));
                collection.add(dragon);
            }
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return collection;
    }

    public synchronized int clearTable(CommandWrapper cm) {
        int rowsAffected = 0;
        try {
            String deleteString = """
                    DELETE FROM Dragon WHERE DRAGON_OWNER = ?;
                    -- SELECT setval('dragon_ID_seq', 1, false);
                    """;
            PreparedStatement stat = this.conn.prepareStatement(deleteString);
            stat.setString(1, cm.getUsername());
            rowsAffected = stat.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("cleared the dragon table");
            } else System.out.println("nothing to delete");

            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        return rowsAffected;
    }

    public synchronized long returnCurrentValue() {
        long currentValue = 0;
        try {
            String selectString = """
                    SELECT currval('dragon_ID_seq');
                    """;
            PreparedStatement stat = this.conn.prepareStatement(selectString);
            ResultSet rs = stat.executeQuery();
            rs.next();
            currentValue = rs.getLong(1);

            stat.close();
            conn.close();
        } catch (
                SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return currentValue;
    }

    public synchronized int updateDragon(DragonX dragon) {
        int rowsAffected = 0;
        try {
            String updateString = """
                    UPDATE dragon
                    SET
                        DRAGON_NAME = ?,
                        DRAGON_AGE = ?,
                        DRAGON_DESC = ?,
                        DRAGON_TYPE = ?,
                        DRAGON_CHARACTER = ?,
                        DRAGON_COORDINATE_X = ?,
                        DRAGON_COORDINATE_Y = ?,
                        KILLER_NAME = ?,
                        KILLER_PASSPORT_ID = ?,
                        KILLER_WEIGHT = ?,
                        KILLER_LOCATION_X = ?,
                        KILLER_LOCATION_Y = ?,
                        KILLER_LOCATION_Z = ?,
                        KILLER_LOCATION_NAME = ?,
                        CREATION_DATE = ?
                    WHERE ID = ?;
                    """;
            PreparedStatement stat = this.conn.prepareStatement(updateString);
            stat.setString(1, dragon.getName());
            stat.setLong(2, dragon.getAge());
            stat.setString(3, dragon.getDescription());
            stat.setString(4, dragon.getType().name());
            stat.setString(5, dragon.getCharacter().name());
            stat.setDouble(6, dragon.getCoordinates().getX());
            stat.setFloat(7, dragon.getCoordinates().getY());
            stat.setString(8, dragon.getKiller().getName());
            stat.setString(9, dragon.getKiller().getPassportID());
            stat.setInt(10, dragon.getKiller().getWeight());
            stat.setInt(11, dragon.getKiller().getLocation().getX());
            stat.setLong(12, dragon.getKiller().getLocation().getY());
            stat.setFloat(13, dragon.getKiller().getLocation().getZ());
            stat.setString(14, dragon.getKiller().getLocation().getName());
            stat.setTimestamp(15, Timestamp.valueOf(dragon.getCreationDate().atStartOfDay()));
            stat.setLong(16, dragon.getId());
            rowsAffected = stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return rowsAffected;
    }
}
