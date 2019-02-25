/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exercicis5;

import java.util.ArrayList;

/**
 *
 * @author Xavier
 */
public class Dept {
   private String codi;
   private String nom;
   private String localitat;
   private  ArrayList <Emp> empleats=null;
   
   public Dept(String codi, String nom,
          String localitat, ArrayList <Emp> empleats){
       this.codi=codi;
       this.nom=nom;
       this.localitat=localitat;
       this.empleats=empleats;
   }
   
   public Dept(String codi, String nom, String localitat){
       this.codi=codi;
       this.nom=nom;
       this.localitat=localitat;
   }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalitat() {
        return localitat;
    }

    public void setLocalitat(String localitat) {
        this.localitat = localitat;
    }

    public ArrayList<Emp> getEmpleats() {
        return empleats;
    }

    public void setEmpleats(ArrayList<Emp> empleats) {
        this.empleats = empleats;
    }
   
   
}
