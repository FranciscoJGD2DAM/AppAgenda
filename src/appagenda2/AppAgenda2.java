/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda2;

import appagenda2.entidades.Provincia;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author usuario
 */
public class AppAgenda2 extends Application {

    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    

    @Override
    public void start(Stage stage) throws IOException {
        
        emf= Persistence.createEntityManagerFactory("AppAgenda2PU");
        em=emf.createEntityManager();
        
        //Si quieres cambiar algunos parametros en el momento de la ejecucion de la aplicacion
        /*
        Map<String,String> emfProperties = new HashMap<String,String>();
        // emfProperties.put("javax.persistence.jdbc.user", "APP");
        // emfProperties.put("javax.persistence.jdbc.password", "App");
        emfProperties.put("javax.persistence.schemageneration.database.action","create");
        EntityManagerFactory emf=
        Persistence.createEntityManagerFactory("AppAgenda2PU",emfProperties);
        EntityManager em = emf.createEntityManager();
        */
        
        em.getTransaction().begin(); //realizar cualquier número de operaciones de modificación de los datos contenidos en la base de datos
        
        em.getTransaction().commit(); //r realizando su volcado definitivo a la base de datos con una orden commit
        
        Provincia provinciaSevilla=new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        em.getTransaction().begin();
        em.persist(provinciaSevilla);
        em.getTransaction().commit();
        
    }
    
    @Override
    public void stop() throws Exception{
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda2;shutdown=true");
        }
        catch(SQLException ex){
            
        } 
    }
    
    public static void main(String[] args){
      launch(args);
      
    }
}
