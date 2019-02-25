/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exercicis4;

import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oIOException;
import com.db4o.query.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Xavier
 */
public class GestorProductes {

    EmbeddedObjectContainer db;

    public GestorProductes() {

    }

    public static void main(String[] args) {
        GestorProductes c = new GestorProductes();
        c.obrirMagatzem();
        System.out.println("----------------------------------");
        System.out.println("Creant articles...");

        c.crearArticle("a1", "Llibre");
        c.crearArticle("a2", "Tovallola");
        c.crearArticle("a3", "Sabates");
        c.crearArticle("a4", "Bufanda");
        c.crearArticle("a5", "Ordenador");
        c.crearArticle("a6", "Portatil");
        c.crearArticle("a7", "Joc");
        c.crearArticle("a8", "Taula");
        c.crearArticle("a9", "Monitor");
        System.out.println("------------------------------------");
        System.out.println("Creant unitats de mesura...");
        c.crearUnitatMesura("cm", "100cm equivalen a 1m");
        c.crearUnitatMesura("mm", "1000mm equivalen a 1m");
        c.crearUnitatMesura("m", "1m equival a 100cm o a 1000mm");

        System.out.println("Creant envasos...");

        c.crearEnvas("capsa", 1000, "cm");
        c.crearEnvas("Lata", 2000, "mm");
        c.crearEnvas("pale", 1500, "m");
        System.out.println("-----------------------------------------");
        System.out.println("Creant productes...");
        c.crearProducte(1500, "Sony", "a1");
        c.crearProducte(900, "Lenovo", "a5");
        c.crearProducte(5.452, "Mango", "a3");
        c.crearProducte(10.232, "Roca", "a2");
        c.crearProducte(15.34, "Primark", "a4");
        c.crearProducte(2.4324, "Adidas", "a1");
        System.out.println("----------------------------------");
        System.out.println("Creant productes envasats");
        c.crearProducteEnvasat("a1", "capsa", "Planeta", 231.432);
        c.crearProducteEnvasat("a7", "capsa", "Meristation", 50.33);
        c.crearProducteAGranel("a8", 575);
        c.crearProducteAGranel("a9", 100);
        c.crearProducteEnvasat("a8", "pale", "Madero", 50);
        System.out.println("------------------------------------");
        System.out.println("Creant magatzems....");
        c.crearMagatzem("m1", "Magatzem gran");
        c.crearMagatzem("m2", "Magatzem petit");
        c.crearMagatzem("m3", "Magatzem mitjà");

        System.out.println("Inserint dades a l'estoc...");

        System.out.println("Inserir Estocs: \n");

        c.inserirDadesEstoc("m1", "Adidas", 2.4324, "a1", 20);
        c.inserirDadesEstoc("m1", "Mango", 5.452, "a3", 30);
        c.inserirDadesEstoc("m1", "Roca", 10.232, "a2", 100);
        c.inserirDadesEstoc("m2", "Sony", 1500, "a1", 50);
        c.inserirDadesEstoc("m3", "Lenovo", 900, "a5", 10);
        c.inserirDadesEstoc("m1", "Planeta", 231.432, "a1", 5);
        c.inserirDadesEstoc("m1", "Meristation", 50.33, "a7", 22);
        c.incrementarEstoc("Adidas", 2.4324, "a1", 10, "m1");
        c.incrementarPreuArticles(5,"a1");
        
        List <Producte> productes=c.obtenirProductesAssignatsAUnArticle("a1");
        for (int i = 0; i < productes.size(); i++) {
            System.out.println(productes.get(i).getPreu()+", "+productes.get(i).getMarca()); 
        }
        

        c.tancarMagatzem();
    }

    public void incrementarPreuArticles(int increment, String idArticle) {
        if (obtenirProductesAssignatsAUnArticle(idArticle) != null) {
            List<Producte> p = obtenirProductesAssignatsAUnArticle(idArticle);
            for (int i = 0; i < p.size(); i++) {
                double preu = p.get(i).getPreu();
                double inc=preu * increment/100;
                p.get(i).setPreu(preu +inc);
                db.store(p.get(i));
                this.updateBaseDeDades();
            }
        }
    }

    public void incrementarEstoc(String marca, double preu, String idArticle, double increment, String idMagatzem) {
        Article a = new Article(idArticle);
        Magatzem m = new Magatzem(idMagatzem);
        if (this.cercarObjecte(a) != null) {
            a = obtenirObjecte(a);
            if (this.cercarObjecte(m) != null) {
                m = obtenirObjecte(m);
                Producte p = (obtenirObjecte(new Producte(a, marca, preu)));
                m.incrementarEstocProducte(p, increment);
                this.db.store(m);
                this.updateBaseDeDades();
            }
        }

    }
    
    public void decrementarEstoc(String marca, double preu, String idArticle, double decrement, String idMagatzem) {
        Article a = new Article(idArticle);
        Magatzem m = new Magatzem(idMagatzem);
        if (this.cercarObjecte(a) != null) {
            a = obtenirObjecte(a);
            if (this.cercarObjecte(m) != null) {
                m = obtenirObjecte(m);
                Producte p = (obtenirObjecte(new Producte(a, marca, preu)));
                m.decrementarEstocProducte(p, decrement);
            }
        }

    }

    public void crearArticle(String id, String descripcio) {
        Article a = this.obtenirObjecte(new Article(id, descripcio));
    }

    public void crearMagatzem(String id, String descripcio) {
        this.obtenirObjecte(new Magatzem(id, descripcio));
    }

    public void crearUnitatMesura(String abreviacio, String descripcio) {
        this.obtenirObjecte(new UnitatDeMesura(abreviacio, descripcio));
    }

    public void crearProducte(double preu, String marca, String idArticle) {
        Article a = new Article(idArticle);
        if (this.cercarObjecte(a) != null) {
            a = obtenirObjecte(a);
            this.obtenirObjecte(new Producte(a, marca, preu));
        }
    }

    public void crearEnvas(String tipus, int quantitat, String simbolUnitat) {
        UnitatDeMesura m = new UnitatDeMesura(simbolUnitat);
        if (this.cercarObjecte(m) != null) {
            m = obtenirObjecte(m);//introduim dins m tots els camps.
            this.obtenirObjecte(new Envas(tipus, quantitat, m));
        }

    }

    public void crearProducteAGranel(String idArticle, double preu) {
        Article a = new Article(idArticle);
        if (this.cercarObjecte(a) != null) {
            a = obtenirObjecte(a);
            this.obtenirObjecte(new ProducteAGranel(a, preu));
        }

    }

    public void crearProducteEnvasat(String idArticle, String tipusEnvas, String marca, double preu) {
        Envas e = new Envas(tipusEnvas);
        Article a = new Article(idArticle);

        if (cercarObjecte(e) != null) {
            e = obtenirObjecte(e);
            if (cercarObjecte(a) != null) {
                a = obtenirObjecte(a);
                this.obtenirObjecte(new ProducteEnvasat(a, marca, preu, e));
            }
        }
    }

    public void borrarObjecte(Object o) {
        try {
            this.db.delete(o);
        } catch (Db4oIOException e) {
            this.db.rollback();
        }

        this.db.commit();
    }

    public void updateBaseDeDades() {
        this.db.commit();
    }

    public List<Producte> obtenirProductesAssignatsAUnArticle(String idArticle) {
        Article a = new Article(idArticle);
        List<Producte> productes = new ArrayList();
        ObjectSet result = this.db.queryByExample(new Producte(a, null, 0));
        while (result.hasNext()) {
            productes.add((Producte) result.next());
        }

        return productes;
    }

    public List<Producte> obtenirProductesAmbCondicions(String denominacio, int min, int max) {
        Query query = this.db.query();
        List<Producte> productes = new ArrayList();
        query.constrain(Producte.class);
        Query node = query.descend("preu");
        Query node2 = query.descend("marca");

        node2.constrain(denominacio).startsWith(true);
        node.constrain(min).greater().and(node.constrain(max).smaller());
        ObjectSet<Producte> set = query.execute();

        for (Producte prod : set) {
            productes.add(set.next());
        }

        return productes;
    }

    public List<Producte> cercarMagatzemEstoc(int valor, String idMagatzem) {
        Magatzem m = new Magatzem(idMagatzem);
        List<Producte> productes = new ArrayList();
        Map<Producte, Estoc> mapa = new HashMap<Producte, Estoc>();
        m = (Magatzem) this.cercarObjecte(m);
        if (m != null) {
            mapa = m.getEstoc();
            Iterator iterador = mapa.entrySet().iterator();

            while (iterador.hasNext()) {
                Map.Entry mapElement = (Map.Entry) iterador.next();
                Estoc e = (Estoc) mapElement.getValue();
                if (e.getQuantitat() <= valor) {
                    productes.add((Producte) mapElement.getKey());
                }
            }
        } else {
            System.out.println("No s'ha trobat el magatzem");
        }

        return productes;
    }

    public Object cercarObjecte(Object o) {
        ObjectSet result = this.db.queryByExample(o);
        if (result.size() == 1) {
            o = result.next();
            return o;
        }
        if (result.size() == 0) {
            System.out.println("Objecte de la classe " + o.getClass() + " no trobat");
        }
        return null;
    }

    public void inserirDadesEstoc(String idMagatzem, String marcaProducte, double preuProducte, String idArticle, double quantitat) {
        Magatzem m = new Magatzem(idMagatzem);
        Article a = new Article(idArticle);
        m = obtenirObjecte(m);
        Producte p = this.obtenirObjecte(new Producte(obtenirObjecte(a), marcaProducte, preuProducte));

        if (p != null) {
            if (m != null) {
                m.assignarEstoc(p, quantitat);
                System.out.println("Estoc inserit amb èxit");
            } else {
                System.out.println("El magatzem no existeix");
            }
        } else {
            System.out.println("El producte no existeix");
        }
        this.db.store(m);
        this.db.commit();
    }

    public Object obtenirObjectes(Object o) {

        ObjectSet result = this.db.queryByExample(o);

        if (result.size() > 1) {
            System.out.println("Patro poc identificatiu");

        }
        if (result.size() == 1) {
            System.out.println("Objecte recuperat de la classe " + o.getClass() + " retornant");
            o = result.next();

        }
        if (result.size() == 0) {
            System.out.println("No s'ha trobat l'objecte a dins la base de dades, per tant, el crearem estam creant un objecte de la classe " + o.getClass());
            this.db.store(o);
            this.db.commit();
        }

        return o;
    }

    public Article obtenirObjecte(Article a) {
        return (Article) obtenirObjectes(a);
    }

    public Magatzem obtenirObjecte(Magatzem m) {
        return (Magatzem) obtenirObjectes(m);
    }

    public Producte obtenirObjecte(Producte p) {
        return (Producte) obtenirObjectes(p);
    }

    public ProducteEnvasat obtenirObjecte(ProducteEnvasat pe) {
        return (ProducteEnvasat) obtenirObjectes(pe);
    }

    public ProducteAGranel obtenirObjecte(ProducteAGranel pag) {
        return (ProducteAGranel) obtenirObjectes(pag);
    }

    public UnitatDeMesura obtenirObjecte(UnitatDeMesura udm) {
        return (UnitatDeMesura) obtenirObjectes(udm);
    }

    public Estoc obtenirObjecte(Estoc e) {
        return (Estoc) obtenirObjectes(e);
    }

    public Envas obtenirObjecte(Envas e) {
        return (Envas) obtenirObjectes(e);
    }

    public List<Magatzem> obtenirMagatzems() {
        Magatzem mag = new Magatzem();
        List<Magatzem> magatzems = new ArrayList();
        ObjectSet result = this.db.queryByExample(mag);

        while (result.hasNext()) {
            magatzems.add((Magatzem) result.next());
        }

        return magatzems;
    }

    public List<Article> obtenirArticles() {
        Article art = new Article();
        List<Article> articles = new ArrayList();
        ObjectSet result = this.db.queryByExample(art);

        while (result.hasNext()) {
            articles.add((Article) result.next());
        }

        return articles;
    }

    public List<Producte> obtenirProductes() {
        Producte p = new Producte();
        List<Producte> productes = new ArrayList();
        ObjectSet result = this.db.queryByExample(p);
        while (result.hasNext()) {
            productes.add((Producte) result.next());
        }

        return productes;
    }

    public List<ProducteAGranel> obtenirProductesAGranel() {
        ProducteAGranel pag = new ProducteAGranel();
        List<ProducteAGranel> productesAGranel = new ArrayList();
        ObjectSet result = this.db.queryByExample(pag);
        while (result.hasNext()) {
            productesAGranel.add((ProducteAGranel) result.next());
        }

        return productesAGranel;
    }

    public List<ProducteEnvasat> obtenirProductesEnvasats() {
        ProducteEnvasat pe = new ProducteEnvasat();
        List<ProducteEnvasat> productesEnvasats = new ArrayList();
        ObjectSet result = this.db.queryByExample(pe);
        while (result.hasNext()) {
            productesEnvasats.add((ProducteEnvasat) result.next());
        }

        return productesEnvasats;
    }

    public List<UnitatDeMesura> obtenirUnitatsDeMesura() {
        UnitatDeMesura udm = new UnitatDeMesura();
        List<UnitatDeMesura> unitatsDeMesura = new ArrayList();
        ObjectSet result = this.db.queryByExample(udm);
        while (result.hasNext()) {
            unitatsDeMesura.add((UnitatDeMesura) result.next());
        }
        return unitatsDeMesura;
    }

    public void tancarMagatzem() {
        this.db.close();
    }

    public void obrirMagatzem() {
        this.db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "src/Dades/dades.ysp");
    }
}
