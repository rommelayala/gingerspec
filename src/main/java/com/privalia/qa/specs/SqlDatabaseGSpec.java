package com.privalia.qa.specs;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlDatabaseGSpec extends BaseGSpec {


    public SqlDatabaseGSpec(CommonG spec) {
        this.commonspec = spec;
    }


    /**
     * Attempts to establish a connection with the given parameters. The DriverManager attempts to select an appropriate driver from the set of registered JDBC drivers.
     * @param isSecured     True if secure connection
     * @param database      Name of the remote database
     * @param dataBaseType  Database type (currently MYSQL/POSTGRESQL)
     * @param host          URL of remote host
     * @param port          Database port
     * @param user          Database user
     * @param password      Database password
     */
    @Given("^I( securely)? connect with JDBC to database '(.+?)' type '(mysql|postgresql)' on host '(.+?)' and port '(.+?)' with user '(.+?)' and password '(.+?)'?$")
    public void connectDatabase(String isSecured, String database, String dataBaseType, String host, String port, String user, String password) {
        try {
            if (isSecured != null) {
                commonspec.getLogger().debug("opening secure database");
                this.commonspec.getSqlClient().connect(host, Integer.parseInt(port), dataBaseType, database, Boolean.parseBoolean(isSecured), user, password);
            } else {
                commonspec.getLogger().debug("opening database");
                this.commonspec.getSqlClient().connect(host, Integer.parseInt(port), dataBaseType, database, Boolean.parseBoolean(isSecured), user, password);
            }
        } catch (ClassNotFoundException | SQLException e) {
            commonspec.getLogger().error("There was a problem connecting to the DB\n{}", e.toString());
        }

        assertThat(this.commonspec.getSqlClient().connectionStatus()).isEqualTo(true);

    }

    /**
     * Close the Database connection
     */
    @Then("^I close database connection$")
    public void connectDatabase() {
        try {
            this.commonspec.getSqlClient().disconnect();
        } catch (SQLException e) {
            commonspec.getLogger().error("Could not close DB connection\n{}", e.toString());
        }

        assertThat(this.commonspec.getSqlClient().connectionStatus()).isEqualTo(false);
    }

    /**
     * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement
     * or an SQL statement that returns nothing, such as an SQL DDL statement.
     * @param query An SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE;
     *              or an SQL statement that returns nothing, such as a DDL statement.
     */
    @When("^I execute query '(.+?)'$")
    public void executeQuery(String query) {

        int result;
        try {
            result = this.commonspec.getSqlClient().executeUpdateQuery(query);
        } catch (SQLException e) {
            assertThat(e.getMessage()).as("A problem was found while executing the query").isEmpty();
        }

    }

    /**
     * Verify if a table exists
     * @param tableName Table name
     */
    @Then("^table '(.+?)' exists$")
    public void verifyTableExists(String tableName) {

        assertThat(this.verifyTable(tableName)).as(String.format("The table %s is not present in the database", tableName)).isTrue();
    }

    /**
     * Verify if a table does not exists
     * @param tableName Table name
     */
    @Then("^table '(.+?)' doesn't exists$")
    public void verifyTableDoesNotExists(String tableName) {

        assertThat(this.verifyTable(tableName)).as(String.format("The table %s is present in the database", tableName)).isFalse();
    }

    /**
     * Executes the given SQL statement, which returns a single ResultSet object.
     * @param query An SQL statement to be sent to the database, typically a static SQL SELECT statement
     */
    @When("^I query the database with '(.+?)'$")
    public void executeSelectQuery(String query) {

        List<List<String>> result = null;
        try {
            result = this.commonspec.getSqlClient().executeSelectQuery(query);
            this.commonspec.setPreviousSqlResult(result);
        } catch (SQLException e) {
            assertThat(e.getMessage()).as("A problem was found while executing the query").isEmpty();
        }

    }

    /**
     * Verifies the results of a SELECT query against a {@link DataTable}
     * @param dataTable
     */
    @Then("^I check that result is:$")
    public void compareTable(DataTable dataTable) {

        List<List<String>> previousResult = this.commonspec.getPreviousSqlResult();
        assertThat(previousResult).as("The last SQL query returned a null result").isNotNull();
        assertThat(previousResult.size()).as("The last SQL query did not returned any rows").isNotEqualTo(0);
        assertThat(dataTable.raw()).as("The returned and the expected results do not match.").isEqualTo(previousResult);

    }

    /**
     * Verify if the content of a table matches the given {@link DataTable}
     * @param tableName Table name
     * @param dataTable {@link DataTable} to match against
     */
    @Then("^I check that table '(.+?)' is iqual to$")
    public void verifyTableContent(String tableName, DataTable dataTable) {

        this.verifyTable(tableName);
        this.executeSelectQuery("SELECT * FROM " + tableName);
        this.compareTable(dataTable);

    }

    /**
     * Executes an SQL from a file. The SQL could be of any kind (a typical SELECT or a SQL Data
     * Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE). If the SQL returns
     * a {@link java.sql.ResultSet}, it is stored internally so further steps can use it
     * @param baseData  File location (typically schemas/myfile.sql)
     */
    @Then("^I execute query from '(.+?)'")
    public void executeQueryFromFile(String baseData) {

        String retrievedData = commonspec.retrieveData(baseData, "string");
        try {
            boolean r = this.commonspec.getSqlClient().executeQuery(retrievedData);

            if (r) {
                this.commonspec.setPreviousSqlResult(this.commonspec.getSqlClient().getPreviousSqlResult());
            }

        } catch (SQLException e) {
            assertThat(e.getMessage()).as("A problem was found while executing the query").isEmpty();
        }
    }

    private boolean verifyTable(String tableName) {

        boolean exists;
        try {
            exists = this.commonspec.getSqlClient().verifyTable(tableName);
        } catch (SQLException e) {
            commonspec.getLogger().error("A problem was found when checking if {} exists: \n{}", tableName, e.toString());
            exists = false;
        }
        return exists;
    }

}