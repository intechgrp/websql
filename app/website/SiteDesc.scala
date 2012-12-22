package website

import models.Page
import models.Page._
import models.DSL._

object SiteDesc {

  val title = "POC WebSql"

  val authentication="select LOGIN from USER where LOGIN = {login} and PASSWORD = {password}"

  val mainMenu = Map[String, String](
    "Liste des Clients" -> "clients",
    "Liste des Comptes" -> "comptes",
    "Rechercher un compte" -> "rechercheCompte?query=",
    "Mes comptes" -> "myAccounts"
  )

  val pages = List[Page](

    page("client") 
      withParameter url("idClient") 
      withQuery "SELECT ID,NOM,PRENOM,ADDRESS,LOGIN FROM CLIENT WHERE ID = {idClient}" 
      withColumn "ID" -> "Identifiant" 
        and "NOM" -> "Nom" 
        and "PRENOM" -> "Prénom" 
        and "ADDRESS" -> "Address" 
        and "LOGIN" -> "Login",

    page("compte") 
      withParameter url("idCompte")
      withQuery "SELECT ID,IBAN,DESCRIPTION,SOLDE,DEVISE,CLIENT FROM COMPTE WHERE ID = {idCompte}",
      
    page("clients") 
      withQuery "SELECT ID,NOM,PRENOM,ADDRESS,LOGIN FROM CLIENT" 
      withColumn "ID" -> "Identifiant" linkedTo "client" as "idClient"
        and "NOM" -> "Nom"
        and "PRENOM" -> "Prénom"
        and "ADDRESS" -> "Adresse"
        and "LOGIN" -> "Login",

    page("comptes") 
      withQuery "SELECT ID,IBAN,DESCRIPTION,SOLDE,DEVISE,CLIENT FROM COMPTE" 
      withColumn "ID" -> "Identifiant" linkedTo "compte" as "idCompte"
      
    
    //TemplatePage("rechercheCompte",{views.html.rechercheCompte(_,_,_)}) fromQuery "Select * from Compte where IBAN like {query}",

    /*ListPage("myAccounts")
      fromQuery "Select COMPTE.IBAN,COMPTE.DESCRIPTION,COMPTE.SOLDE,COMPTE.DEVISE from COMPTE, CLIENT where COMPTE.CLIENT = CLIENT.ID and CLIENT.LOGIN = {username}"
      withTitles List[String]("IBAN","Description","Solde","Devise")
      withAuthentication*/
  )

}








