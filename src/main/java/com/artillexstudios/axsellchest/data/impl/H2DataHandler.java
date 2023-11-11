package com.artillexstudios.axsellchest.data.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.utils.FileUtils;
import com.artillexstudios.axsellchest.utils.Math;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.h2.jdbc.JdbcConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

public class H2DataHandler implements DataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(H2DataHandler.class);
    private Connection connection;

    @Override
    public String getType() {
        return "H2";
    }

    @Override
    public void setup() {
        try {
            connection = new JdbcConnection("jdbc:h2:./" + FileUtils.PLUGIN_DIRECTORY.toFile() + "/data", new Properties(), null, null, false);
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while loading database!", exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `axsellchest_types`(`id` INT AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(64));")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while creating types table!", exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `axsellchest_users`(`uuid` UUID PRIMARY KEY, `name` VARCHAR(16));")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while creating users table!", exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `axsellchest_worlds`(`id` INT AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(64));")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while creating worlds table!", exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `axsellchest_locations`(`id` INT AUTO_INCREMENT PRIMARY KEY, `x` INT, `y` INT, `z` INT, `world_id` INT, FOREIGN KEY(world_id) REFERENCES `axsellchest_worlds`(`id`));")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while creating locations table!", exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `axsellchest_chests`(`id` INT AUTO_INCREMENT PRIMARY KEY, `location_id` INT, `ownerid` UUID, `type_id` TINYINT, `money_made` DOUBLE, `items_sold` BIGINT, `auto_sell` BOOL, `collect_chunk` BOOL, `delete_unsellable` BOOL, `bank` BOOL, `charge` BIGINT, FOREIGN KEY(˙location_id˙) REFERENCES `axsellchest_locations`(`id`), FOREIGN KEY(`owner_id`) REFERENCES `axsellchest_users`(`uuid`), FOREIGN KEY(`type_id`) REFERENCES `axsellchest_types`(`id`));")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while creating chests table!", exception);
        }
    }

    @Override
    public void insertType(ChestType chestType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("MERGE INTO `axsellchest_types`(`name`) KEY(`name`) VALUES(?);")) {
            preparedStatement.setString(1, chestType.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while inserting chest type: {}!", chestType.getName(), exception);
        }
    }

    @Override
    public void loadChestsForWorld(ChestType type, World world) {
        int typeId = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id` FROM `axsellchest_types` WHERE `name` = ?;")) {
            preparedStatement.setString(1, type.getName());

            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                if (resultSet.next()) {
                    typeId = resultSet.getInt("id");
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting type id of type: {}!", type.getName(), exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `chests`.* FROM `axsellchest_chests` AS `chests` JOIN `axsellchest_locations` AS `location` ON `chests`.`location_id` = `location`.`id` WHERE `location`.`world_id` = (SELECT `id` FROM `axsellchest_worlds` WHERE `name` = ?) AND `type_id` = ?;")) {
            preparedStatement.setString(1, world.getName());
            preparedStatement.setInt(2, typeId);

            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                while (resultSet.next()) {
                    int locationId = resultSet.getInt("location_id");
                    UUID ownerUUID = (UUID) resultSet.getObject("owner_id");
                    double moneyMade = resultSet.getDouble("money_made");
                    long itemsSold = resultSet.getLong("items_sold");
                    boolean autoSell = resultSet.getBoolean("auto_sell");
                    boolean collectChunk = resultSet.getBoolean("collect_chunk");
                    boolean deleteUnsellable = resultSet.getBoolean("delete_unsellable");
                    boolean bank = resultSet.getBoolean("bank");
                    long charge = resultSet.getLong("charge");

                    new Chest(type, getLocation(locationId), ownerUUID, itemsSold, moneyMade, locationId, autoSell, collectChunk, deleteUnsellable, bank, charge);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while loading chests of type {} for world {}!", type.getName(), world.getName(), exception);
        }
    }

    @Override
    public int getLocationId(Location location) {
        int worldId = 0;
        World world = location.getWorld();

        if (world == null) {
            return worldId;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("MERGE INTO `axsellchest_worlds`(`name`) KEY(`name`) VALUES(?);", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, world.getName());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    worldId = resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting world id of world: {}!", world.getName(), exception);
            return 0;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("MERGE INTO `axsellchest_locations`(`x`, `y`, `z`, `world_id`) KEY(`x`, `y`, `z`, `world_id`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, Math.round(location.getX()));
            preparedStatement.setInt(2, Math.round(location.getY()));
            preparedStatement.setInt(3, Math.round(location.getZ()));
            preparedStatement.setInt(4, worldId);
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting location id for location: {}!", location, exception);
        }

        return 0;
    }

    @Override
    public Location getLocation(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `axsellchest_locations` WHERE `id` = ?;")) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int worldId = resultSet.getInt("world_id");
                    int x = resultSet.getInt("x");
                    int y = resultSet.getInt("y");
                    int z = resultSet.getInt("z");

                    return new Location(getWorld(worldId), x, y, z);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting location from location id: {}!", id, exception);
        }

        return null;
    }

    @Override
    public World getWorld(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `name` FROM `axsellchest_worlds` WHERE `id` = ?;")) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Bukkit.getWorld(resultSet.getString("name"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting world from world id: {}!", id, exception);
        }

        return null;
    }

    @Override
    public void saveChest(Chest chest) {
        int locationId = chest.getLocationId();
        int type = 0;
        UUID userId = null;

        if (locationId == 0) {
            locationId = getLocationId(chest.getLocation());
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `axsellchest_types` WHERE `name` = ?;")) {
            preparedStatement.setString(1, chest.getType().getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    type = resultSet.getInt("id");
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting type id of type: {}!", chest.getType().getName(), exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("MERGE INTO `axsellchest_users`(`uuid`, `name`) KEY(`uuid`) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, chest.getOwnerUUID());
            preparedStatement.setString(2, chest.getOwner().getName());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    userId = (UUID) resultSet.getObject(1);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while saving user: (Name: {}, UUID: {})", chest.getOwner().getName(), chest.getOwnerUUID(), exception);
            return;
        }

        if (userId == null) {
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("MERGE INTO `axsellchest_chests`(`location_id`, `owner_id`, `type_id`, `money_made`, `items_sold`, `auto_sell`, `collect_chunk`, `delete_unsellable`, `bank`, `charge`) KEY(`location_id`) VALUES(?,?,?,?,?,?,?,?,?,?);")) {
            preparedStatement.setInt(1, locationId);
            preparedStatement.setObject(2, chest.getOwnerUUID());
            preparedStatement.setInt(3, type);
            preparedStatement.setDouble(4, chest.getMoneyMade());
            preparedStatement.setLong(5, chest.getItemsSold());
            preparedStatement.setBoolean(6, chest.isAutoSell());
            preparedStatement.setBoolean(7, chest.isCollectChunk());
            preparedStatement.setBoolean(8, chest.isDeleteUnsellable());
            preparedStatement.setBoolean(9, chest.isBank());
            preparedStatement.setLong(10, chest.getCharge());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while saving chest at location: {}", chest.getLocation(), exception);
        }
    }

    @Override
    public void deleteChest(Chest chest) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `axsellchest_chests` WHERE `location_id` = ?;")) {
            preparedStatement.setInt(1, chest.getLocationId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while deleting chest: {}.", chest, exception);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `axsellchest_chests` WHERE `location_id` = ?;")) {
            preparedStatement.setInt(1, chest.getLocationId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `axsellchest_locations` WHERE `id` = ?;")) {
                        statement.setInt(1, chest.getLocationId());
                        statement.executeUpdate();
                    }
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while deleting chest: {}.", chest, exception);
        }
    }

    @Override
    public int getChests(UUID uuid) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(`owner_id`) FROM `axsellchest_chests` WHERE `owner_id` = ?;")) {
            preparedStatement.setObject(1, uuid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while getting chests for uuid: {}.", uuid, exception);
        }

        return 0;
    }

    @Override
    public void disable() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SHUTDOWN DEFRAG;")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An unexpected error occurred while disabling the database.", exception);
        }
    }
}
