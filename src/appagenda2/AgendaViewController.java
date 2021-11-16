/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda2;

import appagenda2.entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;

/**
 * FXML Controller class
 *
 * @author frang
 */
public class AgendaViewController implements Initializable {
  
    private Persona personaSeleccionada;
    
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona,String> columnNombre;
    @FXML
    private TableColumn<Persona,String> columnApellidos;
    @FXML
    private TableColumn<Persona,String> columnEmail;
    @FXML
    private TableColumn<Persona,String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private AnchorPane rootAgendaView;
    
    private TableView tableViewPrevio;
    private Persona persona;
    private boolean nuevaPersona;

    
    @Override
    public void initialize(URL url, ResourceBundle rb){ //asocia la id de la columna con la columnas de la base de datos
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); 
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(cellData->{
            SimpleStringProperty property = new SimpleStringProperty();
            if (cellData.getValue().getProvincia()!=null){
                property.setValue(cellData.getValue().getProvincia().getNombre());  // Muestra el nombre de la provincia
            }
            return property;
        });
        
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener( // muestra los datos selecionados de la tabla en los text field
        (observable,oldValue,newValue)->{
            
            personaSeleccionada=newValue;
            if (personaSeleccionada != null){
        
                textFieldNombre.setText(personaSeleccionada.getNombre());
                textFieldApellidos.setText(personaSeleccionada.getApellidos());
            }else{
                textFieldNombre.setText("");
                textFieldApellidos.setText("");
            }
        });
        
    }
    public void cargarTodasPersonas(){ //muestra todas las personas
        javax.persistence.Query queryPersonaFindAll= entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona= queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona)
        );
    }
    
    public void setEntityManager(EntityManager entityManager){
    this.entityManager=entityManager;
    }
    
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        if (personaSeleccionada != null){
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());

            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();

            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);

            TablePosition pos = new TablePosition(tableViewAgenda,numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        } else {
            textFieldNombre.setText("");
            textFieldApellidos.setText("");
        }
        
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("SesionView.fxml"));
        Parent rootDetalleView=fxmlLoader.load();
        
        rootAgendaView.setVisible(false);
        
        StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
        rootMain.getChildren().add(rootDetalleView);

        
        SesionViewController sesionViewController = (SesionViewController) fxmlLoader.getController();
        sesionViewController.setRootAgendaView((AnchorPane) rootAgendaView);
        
        sesionViewController.setTableViewPrevio(tableViewAgenda);//Intercambio de datos funcionales con el detalle
       
        
        // Para el botón Editar
        if(personaSeleccionada != null){
            sesionViewController.setPersona(entityManager, personaSeleccionada,false);

            sesionViewController.mostrarDatos();
        }
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.get() == ButtonType.OK){

         // Acciones a realizar si el usuario acepta
        entityManager.getTransaction().begin();
        entityManager.merge(personaSeleccionada);
        entityManager.remove(personaSeleccionada);
        entityManager.getTransaction().commit();
        tableViewAgenda.getItems().remove(personaSeleccionada);
        tableViewAgenda.getFocusModel().focus(null);
        tableViewAgenda.requestFocus();

        } else {
            
        // Acciones a realizar si el usuario cancela
        int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
        tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
        TablePosition pos = new TablePosition(tableViewAgenda,
        numFilaSeleccionada,null);
        tableViewAgenda.getFocusModel().focus(pos);
        tableViewAgenda.requestFocus();
        }

        
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) { //Pulsando el boton nuevo muestra el otro fxml llamado SesionView
        try{
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("SesionView.fxml"));
            Parent rootDetalleView=fxmlLoader.load();
            // Ocultar la vista de la lista
            
             //Añadir la vista detalle al StackPane principal para que se muestre
            
            
            SesionViewController sesionViewController = (SesionViewController) fxmlLoader.getController();
            sesionViewController.setRootAgendaView(rootAgendaView);

            sesionViewController.setTableViewPrevio(tableViewAgenda);//Intercambio de datos funcionales con el detalle
 
            // Para el botón Nuevo:
            personaSeleccionada = new Persona();
            sesionViewController.setPersona(entityManager, personaSeleccionada,true);
            sesionViewController.mostrarDatos();
            
            rootAgendaView.setVisible(false);// Ocultar la vista de la lista
            
            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            } catch (IOException ex){
                Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
            }
               
    
    }
    
     
    
}
