/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exercicis5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.BaseXServer;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.query.QueryException;

/**
 *
 * @author Xavier
 */
public class GestorDB {
    BaseXServer server;
    
    public GestorDB() {
        try {
            this.server = new BaseXServer();
        } catch (IOException ex) {
            Logger.getLogger(GestorDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

    public BaseXServer getServer() {
        return server;
    }

    public void setServer(BaseXServer server) {
        this.server = server;
    }

    public static void main(String[] args) throws QueryException {
        GestorDB gdb = new GestorDB();
        Exercicis5 ex5 = new Exercicis5();
        try {

            ex5.recuperarEmpleats();
            ex5.recuperarDepartaments();
            //ex5.deleteDept("d451");
            //Inserir nou departament amb empleats.
            //ex5.insertDept(gdb.inserirDepartament().getCodi(), gdb.inserirDepartament().getNom(), gdb.inserirDepartament().getLocalitat(), gdb.inserirDepartament().getEmpleats());
            for (int i = 0; i < ex5.getEmpleats().size(); i++) {
                System.out.println(ex5.getEmpleats().get(i).getCodiDept());
            }
            ex5.tancarSessio();
            gdb.tancarServer();
        } catch (IOException ex) {
            Logger.getLogger(Exercicis5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tancarServer() {
        this.server.stop();
    }

    public Dept inserirDepartament() {
        ArrayList<Emp> empleats = new ArrayList();
        Emp e = new Emp("e34", "Martinez", "Fuster", "20/04/17", 10000, 1000, "d30", "e7902");
        Emp e2 = new Emp("e35", "Gonzalez", "Ferrer", "21/05/18", 1000, 1, "d30", "e7432");
        empleats.add(e);
        empleats.add(e2);

        Dept d = new Dept("d451", "Fusteria", "Manzanares", empleats);

        return d;
    }

}
