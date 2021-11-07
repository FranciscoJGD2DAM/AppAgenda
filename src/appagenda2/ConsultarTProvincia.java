/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda2;

import appagenda2.entidades.Provincia;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author frang
 */
public class ConsultarTProvincia extends Application{

    private EntityManagerFactory emf;
    private EntityManager em;
    
    
    @Override
    public void start(Stage stage) throws IOException {
        
        emf= Persistence.createEntityManagerFactory("AppAgenda2PU");
        em=emf.createEntityManager();
        /*
        Query queryProvincias = em.createNamedQuery("Provincia.findAll"); //Muestra todo la columnas de la tabla provincias
        
        List<Provincia> listProvincias = queryProvincias.getResultList(); // Se crea una lista que encontrara todos los objetos que existian en la tabla
        
        
        //Creamos un for para recorrer la lista y mostrar todo el contenido
        for(Provincia provincia : listProvincias){
        System.out.println(provincia.getNombre());
        }
        */

        //obtener los objetos Provincia cuyo nombre sea "Cádiz"
        /* 
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz =queryProvinciaCadiz.getResultList();
        for(Provincia provinciaCadiz:listProvinciasCadiz){
        System.out.println(provinciaCadiz.getId()+":");
        System.out.println(provinciaCadiz.getNombre());
        }
        */
        
        
        //Obetener el objeto Provincia cuyo id es 2
        /*
        Provincia provinciaId2=em.find(Provincia.class,2);
        if (provinciaId2 != null){
        System.out.print(provinciaId2.getId() + ":");
        System.out.println(provinciaId2.getNombre());
        } else {
        System.out.println("No hay ninguna provincia con ID=2");
        }
        */

        
        // Modificar Base. Asigna el codigo "CA" a aquellos objetos cuyo nombre sea "Cadiz" y actuliza los cambios
        
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz =queryProvinciaCadiz.getResultList();
        em.getTransaction().begin();
        for(Provincia provinciaCadiz : listProvinciasCadiz){
        provinciaCadiz.setCodigo("CA");
        em.merge(provinciaCadiz);
        }
        em.getTransaction().commit();
        
        
    }
    public static void main(String[] args) {
        
   
    }
    
}
