package com.example;

import java.sql.*;

public class DBTransfer {

    public static void main(String[] args) {
        String serverUrl = "jdbc:mysql://[82.115.24.110]:3306/[productdb]";
        String serverUser = "[server_user]";
        String serverPassword = "[server_password]";

        String localUrl = "jdbc:mysql://localhost:3306/[local_database]";
        String localUser = "[local_user]";
        String localPassword = "[local_password]";

        String tableName = "[your_table]"; // change it

        try {
            Connection serverConn = DriverManager.getConnection(serverUrl, serverUser, serverPassword);
            Connection localConn = DriverManager.getConnection(localUrl, localUser, localPassword);

            Statement stmt = serverConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            // Get column count and metadata from server database
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Build the INSERT statement dynamically
            StringBuilder insertBuilder = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
            for (int i = 1; i <= columnCount; i++) {
                insertBuilder.append("?");
                if (i < columnCount) {
                    insertBuilder.append(", ");
                }
            }
            insertBuilder.append(")");

            PreparedStatement localStmt = localConn.prepareStatement(insertBuilder.toString());

            int batchSize = 100;
            int count = 0;

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    // Determine the column type and retrieve data accordingly
                    int columnType = metaData.getColumnType(i);
                    switch (columnType) {
                        case Types.INTEGER:
                            localStmt.setInt(i, rs.getInt(i));
                            break;
                        case Types.VARCHAR:
                            localStmt.setString(i, rs.getString(i));
                            break;
                        case Types.BOOLEAN:
                            localStmt.setBoolean(i, rs.getBoolean(i));
                            break;
                        case Types.DATE:
                            localStmt.setDate(i, rs.getDate(i));
                            break;
                        case Types.TIMESTAMP:
                            localStmt.setTimestamp(i, rs.getTimestamp(i));
                            break;
                        // Add more data types as needed
                        default:
                            localStmt.setObject(i, rs.getObject(i));
                            break;
                    }
                }
                localStmt.addBatch();
                count++;

                if (count % batchSize == 0) {
                    localStmt.executeBatch(); // Execute the batch
                    System.out.println("Executed batch: " + count);
                }
            }

            localStmt.executeBatch(); // Execute any remaining records
            System.out.println("Transfer complete. Total records transferred: " + count);

            rs.close();
            stmt.close();
            localStmt.close();
            serverConn.close();
            localConn.close();

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
}