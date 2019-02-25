/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exercicis5;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.query.*;

/**
 *
 * @author Xavier
 */
public class Exercicis5 {

    ArrayList<Dept> departaments;
    ArrayList<Emp> empleats;
    private ClientSession session;

    /**
     * @param args the command line arguments
     */
    public Exercicis5() {
        this.departaments = new ArrayList();
        this.empleats = new ArrayList();

        //crearDB i conectar si està creada només l'obri.
        try {
            this.session = new ClientSession("localhost", 1984, "admin", "admin");
            if (this.session.execute(new Open("empresa")) != null) {
                this.session.execute(new Open("empresa"));
                System.out.println("\n* Create a database.");
            } else {
                this.session.execute(new CreateDB("empresa", "src/Dades/empresa.xml"));
            }
        } catch (IOException ex) {
            Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//-->


    public ArrayList<Dept> getDepartaments() {
        return departaments;
    }

    public void setDepartaments(ArrayList<Dept> departaments) {
        this.departaments = departaments;
    }

    public ArrayList<Emp> getEmpleats() {
        return empleats;
    }

    public void setEmpleats(ArrayList<Emp> empleats) {
        this.empleats = empleats;
    }

    public void tancarSessio() throws BaseXException {
        try {
            this.session.close();
            System.out.println("base de dades tancada amb èxit");
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }//-->

    public void recuperarDepartaments() throws IOException {//Persistencia departaments.
        String[] queries = {"for $x in doc('src/Dades/empresa.xml')//nom return data($x)",
            "for $x in doc('src/Dades/empresa.xml')//localitat return data($x)",
            "//dept/@codi/string()"};
        String count = "count(empresa/departaments/dept)";
        ClientQuery c = this.session.query(count);
        int limit = Integer.parseInt(c.execute());

        for (int i = 0; i < queries.length; i++) {
            ClientQuery query = this.session.query(queries[i]);
            for (int j = 0; j < limit; j++) {
                switch (i) {
                    case 0:
                        this.departaments.add(new Dept(null, query.next(), null));
                        break;
                    case 1:
                        this.departaments.get(j).setLocalitat(query.next());
                        break;
                    case 2:
                        this.departaments.get(j).setCodi(query.next());
                        break;
                }
            }
        }
    }

    public void recuperarEmpleats() throws IOException {//Persistència departaments.
        String[] queries = {"for $x in doc('src/Dades/empresa.xml')//cognom return data($x)",
            "for $x in doc('src/Dades/empresa.xml')//ofici return data($x)", "//emp/@codi/string()",
            "for $x in doc('src/Dades/empresa.xml')//dataAlta return data($x)",
            "for $x in doc('src/Dades/empresa.xml')//salari return data($x)",
            "for $x in doc('src/Dades/empresa.xml')//comissio return data($x)",
            "//emp/@dept/string()", "//emp/@cap/string()"};//Consultes

        String count = "count(empresa/empleats/emp)";
        ClientQuery c = this.session.query(count);
        
        int limit = Integer.parseInt(c.execute());
        
        for (int i = 0; i < queries.length; i++) {//nº Queries
            ClientQuery query = this.session.query(queries[i]);
            for (int j = 0; j < limit; j++) {//nº empleats
                switch (i) {
                    case 0:
                        this.empleats.add(new Emp(null, query.next(), null, null, 0, 0, null, null));
                        break;
                    case 1:
                        this.empleats.get(j).setOfici(query.next());
                        break;
                    case 2:
                        this.empleats.get(j).setCodi(query.next());
                        break;
                    case 3:
                        this.empleats.get(j).setDataAlta(query.next());
                        break;
                    case 4:
                        this.empleats.get(j).setSalari(Integer.parseInt(query.next()));
                        break;
                    case 5:
                        if (query.next() != null) {
                            this.empleats.get(j).setComissio(Integer.parseInt(query.next()));
                        }
                        break;
                    case 6:
                        this.empleats.get(j).setCodiDept(query.next());
                        break;
                    case 7:
                        this.empleats.get(j).setCodiCap(query.next());
                }
            }
        }
    }

    public ArrayList<Dept> getDeptSenseEmpleat() {

        ArrayList<Dept> departamentsSenseEmpleats = new ArrayList();

        for (int i = 0; i < this.departaments.size(); i++) {
            if (this.departaments.get(i).getEmpleats() == null) {
                departamentsSenseEmpleats.add(this.departaments.get(i));
            }
        }

        return departamentsSenseEmpleats;
    }

    public ArrayList<Dept> getDeptAmbEmpleats() {
        ArrayList<Dept> departamentsAmbEmpleats = new ArrayList();

        for (int i = 0; i < this.departaments.size(); i++) {
            if (this.departaments.get(i).getEmpleats() != null) {
                departamentsAmbEmpleats.add(this.departaments.get(i));
            }
        }

        return departamentsAmbEmpleats;
    }

    public void insertDept(String codi, String nom, 
            String localitat, ArrayList<Emp> empleats) {
        Scanner sc = new Scanner(System.in);
        String emp = "";
        String totsEmp = "";

        String doc = "<dept codi='" + codi + "'> "
                + "<nom>" + nom + "</nom>"
                + "<localitat>" + localitat + "</localitat>"
                + "</dept>";

        try {
            ClientQuery insertarDep = this.session.query("insert node "
                    + doc + " after //dept[@codi='d40']");
            if (cercarDepartament(codi) == null) {
                for (int i = 0; i < empleats.size(); i++) {
                    
                    String sal = Integer.toString(empleats.get(i).getSalari());
                    String com = Integer.toString(empleats.get(i).getSalari());
                    
                    emp = "<emp codi='" + empleats.get(i).getCodi() + "' " + "dept='"
                            + codi + "' cap='" + empleats.get(i).getCodiCap() + "'>\n<cognom>" 
                            + empleats.get(i).getCognom() + "</cognom>\n<ofici>" 
                            + empleats.get(i).getOfici() + "</ofici>\n<dataAlta>"
                            + empleats.get(i).getDataAlta() + "</dataAlta>\n" + "<salari>" 
                            + sal + "</salari>\n" + "<comissio>" + com + "</comissio>\n" + "</emp>\n";
                    ClientQuery insertarEmp = this.session.query("insert node " 
                            + emp + " after //emp[@codi='e7934']");
                    insertarEmp.execute();
                    this.empleats.add(empleats.get(i));
                }
                this.departaments.add(new Dept(codi, nom, localitat, empleats));
                insertarDep.execute();
            } else {
                System.out.println("D'acord, no s'ha insertat el "
                + "departament a la base de dades, perque ja existeix");
            }

        } catch (IOException ex) {
            Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteDept(String idDept) {
        Dept d = cercarDepartament(idDept);
        Scanner sc = new Scanner(System.in);
        String resposta = "";

        try {
            ClientQuery deleteDep = this.session.query("delete node //dept[@codi='" + idDept + "']");
            if (d != null) {
                System.out.print("Quan borrem el departament amb id" 
                + idDept + " també vols borrar els seus empleats? Borrar o resituar els empleats? ");
                resposta = sc.nextLine();
                if (resposta.charAt(0) == 'B' || resposta.charAt(0) == 'b') {
                    deleteEmpleatsAssignatsADepartament(idDept);
                    System.out.println("Empleats del departament" + idDept + " borrats emb èxit");
                } else if (resposta.charAt(0) == 'R' || resposta.charAt(0) == 'r') {
                    System.out.print("A quin departament vols assignar els empleats? ");
                    resposta = sc.nextLine();
                    inserirEmpleats(cercarEmpleatsPerDepartament(idDept),resposta);
                    deleteEmpleatsAssignatsADepartament(idDept);
                }

                deleteDep.execute();
                this.departaments.remove(d);
                System.out.println("Departament amb id " + idDept + " borrat");
            } else {
                System.out.println("No es pot borrar el departament perque no existeix");
            }

        } catch (IOException ex) {
            Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    public void deleteEmpleatsAssignatsADepartament(String idDept) {
        String queryEmpleats = "delete node //emp[@dept='" + idDept + "']";
        try {
            ClientQuery deleteEmp = this.session.query(queryEmpleats);
            deleteEmp.execute();
            borrarEmpleatsDeUnDepartament(idDept);
        } catch (IOException ex) {
            Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void inserirEmpleats(ArrayList<Emp> empleats,String idDept){
        String emp="";
        for (int i = 0; i < empleats.size(); i++) {
            emp = "<emp codi='" + empleats.get(i).getCodi() + "' " + "dept='"
            + idDept + "' cap='" + empleats.get(i).getCodiCap() + "'>\n<cognom>"
            + empleats.get(i).getCognom() + "</cognom>\n<ofici>"
            + empleats.get(i).getOfici() + "</ofici>\n<dataAlta>"
            + empleats.get(i).getDataAlta() + "</dataAlta>\n" + "<salari>" 
            + empleats.get(i).getSalari() + "</salari>\n" + "<comissio>"
            + empleats.get(i).getComissio() + "</comissio>\n" + "</emp>\n";
            
            try {
                ClientQuery insertarEmp = this.session.query("insert node " + emp + " after //emp[@codi='e7934']");
                insertarEmp.execute();
                this.empleats.add(empleats.get(i));
                this.empleats.get(i).setCodiDept(idDept);
            } catch (IOException ex) {
                Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
    }

    public ArrayList<Emp> cercarEmpleatsPerDepartament(String idDept) {
        ArrayList<Emp> empleats = new ArrayList();
        for (int i = 0; i < this.empleats.size(); i++) {
            if (this.empleats.get(i).getCodiDept().equals(idDept)) {
                empleats.add(this.empleats.get(i));
                System.out.println(this.empleats.get(i).getCodi());
            }
        }
        
        borrarEmpleatsDeUnDepartament(idDept);

        return empleats;
    }
    
    

    public void borrarEmpleatsDeUnDepartament(String idDept) {
        for (int i = 0; i < this.empleats.size(); i++) {
            if (this.empleats.get(i).getCodiDept().equals(idDept)) {
                this.empleats.remove(i);
            }
        }
    }

    public Dept cercarDepartament(String idDept) {
        Dept d = null;
        for (int i = 0; i < this.departaments.size(); i++) {
            if (this.departaments.get(i).getCodi().equals(idDept)) {
                d = this.departaments.get(i);
            }
        }

        return d;
    }

    public Emp cercarEmpleat(String idEmpleat) {
        Emp e = null;
        for (int i = 0; i < this.empleats.size(); i++) {
            if (this.empleats.get(i).getCodi().equals(idEmpleat)) {
                e = this.empleats.get(i);
            }
        }

        return e;
    }

    public void crearDb(String nom, String direccio) {
        try {
            this.session.execute(new CreateDB(nom, direccio));
            System.out.println("Base de dades " + nom + " creada amb èxit");
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void borrarDb(String nom) {
        try {
            this.session.execute(new DropDB(nom));
            System.out.println("Base de dades borrada amb èxit");
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void obrirDb(String baseDeDades) {
        try {
            this.session.execute(new Open(baseDeDades));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void mostrarBasesDeDades() {
        try {
            String a = this.session.execute(new List());
            System.out.println(a);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

}
