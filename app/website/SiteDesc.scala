package website

import models._
import models.Page._
import models.DSL._

object SiteDesc {

  val title = "POC WebSql"

  val dbUrl = "jdbc:h2:mem:websql"
  val dbDriver = "org.h2.Driver"

  val authentication="select LOGIN from USER where LOGIN = ? and PASSWORD = ?"

  val mainMenu = Map[String, String](
    "Liste des Clients"     -> "clients",
    "Liste des Comptes"     -> "comptes",
    "Compte master/detail"  -> "comptesWithDetail",
    "Rechercher un compte"  -> "rechercheCompte",
    "Mes comptes"           -> "myAccounts"
  )

  val pages = List[Page](

    /* List of all clients, with a link to detail page of a client */
    listPage("clients") 
      withQuery "SELECT ID,NOM,PRENOM,ADRESSE,LOGIN FROM CLIENT" 
      withColumn 
            "ID"       -> "Identifiant" linkedTo "client" as "idClient" named "Afficher le détail"
        and "NOM"      -> "Nom"
        and "PRENOM"   -> "Prénom"
        and "ADRESSE"  -> "Adresse"
        and "LOGIN"    -> "Login",


    /* Detail page of a client */
    detailPage("client") 
      withParameter url("idClient") 
      withQuery "SELECT ID,NOM,PRENOM,ADRESSE,LOGIN FROM CLIENT WHERE ID = {idClient}" 
      withColumn 
            "ID"       -> "Identifiant" 
        and "NOM"      -> "Nom" 
        and "PRENOM"   -> "Prénom" 
        and "ADRESSE"  -> "Address" 
        and "LOGIN"    -> "Login",


    /* List of all accounts, with a link to account detail page */
    listPage("comptes")
      withQuery "SELECT ID,IBAN,DESCRIPTION,SOLDE,DEVISE,CLIENT FROM COMPTE" 
      withColumn 
        "ID" -> "Identifiant" linkedTo "compte" as "idCompte",
      

    /* Detail page of an account */
    detailPage("compte") 
      withParameter url("idCompte")
      withQuery "SELECT ID,IBAN,DESCRIPTION,SOLDE,DEVISE,CLIENT FROM COMPTE WHERE ID = {idCompte}",

    
    /* Secured page, listing account list filtered with the current user */
    listPage("myAccounts").withAuthentication
      withQuery 
        """ select COMPTE.IBAN,COMPTE.DESCRIPTION,COMPTE.SOLDE,COMPTE.DEVISE
            from COMPTE, CLIENT
            where COMPTE.CLIENT = CLIENT.ID and CLIENT.LOGIN = {username}"""
      withColumn 
            "IBAN"     -> "IBAN"
        and "DESCRIPTION" -> "Description"
        and "SOLDE"    -> "Solde"
        and "DEVISE"   -> "Devise",


    /* Custom form-based (POST) search page */
    customPage("rechercheCompte",views.html.rechercheCompte.apply _)
      withParameter form("iban") and form("description")
      withQuery "select * from Compte where IBAN like {iban} or DESCRIPTION = {description}"
      withQuery "listOfDescription" -> "SELECT DISTINCT DESCRIPTION FROM COMPTE"

    /* Master-detail page for comptes */
    // TODO : need new feature for "mandatory parameters" (if not set, then the query cannot be executed)
    /*masterDetailPage("comptesWithDetail")
      withParameter url("idCompte")
      withQuery "list"    -> "select * from COMPTE"
        withColumn 
          "ID" -> "Detail" linkedTo "comptesWithDetail" as "idCompte" named "Afficher le détail"
      withQuery "detail"  -> "select * from COMPTE where ID = {idCompte}"*/
      

  )

}








