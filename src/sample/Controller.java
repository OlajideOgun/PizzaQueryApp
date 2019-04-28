package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;


// Fig. 24.29: DisplayQueryResultsController.java
// Controller for the DisplayQueryResults app
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.regex.PatternSyntaxException;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class Controller {

    @FXML
    private Button queryLastNameSearchButton;

    @FXML
    private TextField queryLastNameTextField;

    @FXML
    private Label queryLastNameLabel;

    @FXML
    private Label queryPhoneLabel;

    @FXML
    private Button queryPhoneSearchButton;

    @FXML
    private Button rangeQuerySearchButton;

    @FXML
    private Button simpleQuerySearchButton;

    @FXML
    private TextField queryPhoneTextField;

    @FXML
    private Label rangeQueriesLabel;

    @FXML
    private Label simpleQueriesLabel;

    @FXML
    private Label enterFilterTextLabel;

    @FXML
    private Button applyFilterButton;

    @FXML
    private Label numberofOrdersLabel;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button submitQueryButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField filterTextField;

    @FXML
    private TextArea queryTextArea;


    // database URL, username and password
    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/pizzadb";
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";

    // default query retrieves all data from Authors table
    private static final String DEFAULT_QUERY = "SELECT * FROM orders";
    private static final String NUM_ORDERS_PRICE_PER_DAY_QUERY
            = "select date(ordertime) as Order_Day, count(*) as Order_Number, sum(totalprice) as   Order_Total \n"
            + "from orders \n"
            + "group by date(ordertime) \n"
            + "order by order_day desc";
    private static final String ORDER_SEARCH_BY = "select firstname, lastname, ordertime, pizza.pizzades, ordereditems.pizzaqn, sides.sidesdes, ordereditems.sidesqn, drinks.drinkdes, ordereditems.drinkqn\n"
            + "from customers\n"
            + "inner join orders on customers.phonenumber = orders.phonenumber\n"
//            + "inner join customers on orders.lastname = customers.lastname\n"
            + "inner join ordereditems on ordereditems.orderid = orders.orderid\n"
            + "inner join pizza on pizza.pizzaid = ordereditems.pizzaid\n"
            + "inner join sides on sides.sidesid=ordereditems.sidesid\n"
            + "inner join drinks on drinks.drinkid=ordereditems.drinkid\n";


    // used for configuring JTable to display and sort data
    private ResultSetTableModel tableModel;
    private TableRowSorter<TableModel> sorter;

    public void initialize() {
        queryTextArea.setText(DEFAULT_QUERY);

        // create ResultSetTableModel and display database table
        try {
            // create TableModel for results of DEFAULT_QUERY
            tableModel = new ResultSetTableModel(DATABASE_URL,
                    USERNAME, PASSWORD, DEFAULT_QUERY);

            // create JTable based on the tableModel
            JTable resultTable = new JTable(tableModel);

            // set up row sorting for JTable
            sorter = new TableRowSorter<TableModel>(tableModel);
            resultTable.setRowSorter(sorter);

            // configure SwingNode to display JTable, then add to borderPane
            SwingNode swingNode = new SwingNode();
            swingNode.setContent(new JScrollPane(resultTable));
            borderPane.setCenter(swingNode);
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());
            tableModel.disconnectFromDatabase(); // close connection
            System.exit(1); // terminate application
        }
    }

    // query the database and display results in JTable
    @FXML
    void submitQueryButtonPressed(ActionEvent event) {
        // perform a new query
        try {
            tableModel.setQuery(queryTextArea.getText());
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // close connection
                System.exit(1); // terminate application
            }
        }
    }

    // apply specified filter to results
    @FXML
    void applyFilterButtonPressed(ActionEvent event) {
        String text = filterTextField.getText();

        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(text));
            } catch (PatternSyntaxException pse) {
                displayAlert(AlertType.ERROR, "Regex Error",
                        "Bad regex pattern");
            }
        }
    }

    // display an Alert dialog
    private void displayAlert(
            AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // execute simple query and display results in table
    public void simpleQuerySearchButtonPressed(ActionEvent actionEvent) {
        // perform a new query
        try {
            tableModel.setQuery(NUM_ORDERS_PRICE_PER_DAY_QUERY);
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // close connection
                System.exit(1); // terminate application
            }
        }


    }


    //query to produce results by given last name
    public void queryLastNameSearchButtonPressed(ActionEvent actionEvent) {
        //string with where clause
        String NEW_ORDER_SEARCH_BY = ORDER_SEARCH_BY + " where customers.lastname =" + "'" + queryLastNameTextField.getText() + "'";


//         perform a new query
        try {
            tableModel.setQuery(NEW_ORDER_SEARCH_BY);
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // close connection
                System.exit(1); // terminate application
            }
        }

    }

    //query to produce results by given phone number
    public void queryPhoneSearchButtonPressed(ActionEvent actionEvent) {
//        System.out.println(queryPhoneTextField.getText());
//        System.out.println(queryPhoneTextField.getText().getClass());
//        System.out.println(queryPhoneTextField.getText().length());

        //string with where clause
        String NEW_ORDER_SEARCH_BY1 = ORDER_SEARCH_BY + " where orders.phonenumber =" + "'" + queryPhoneTextField.getText() + "'";
//         perform a new query
        try {
            tableModel.setQuery(NEW_ORDER_SEARCH_BY1);
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // close connection
                System.exit(1); // terminate application
            }
        }

    }
    //query to produce results by given dates
    public void rangeQuerySearchButtonPressed(ActionEvent actionEvent) {
        //transform date value to string
        String fromdate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String todate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //query with where clause
        String searchString = DEFAULT_QUERY + " where date(OrderTime) >= '" + fromdate + "' AND date(OrderTime) <= '" + todate + "'";

        // perform a new query
        try {
            tableModel.setQuery(searchString);
        } catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // close connection
                System.exit(1); // terminate application
            }
        }
    }


}



