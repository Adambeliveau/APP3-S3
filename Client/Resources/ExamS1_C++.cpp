/*Adam Béliveau 
examen 3*/

#include<iostream>
#include<iomanip>
#include<fstream>
#include<locale>
#include<string>

using namespace std;

const int nbOpMax = 1000;
int nbOp = 0;

struct operation
{
   int numCompte;
   string nom;
   string prenom;
   char typeOp;
   double montantOp;
};

void lireFichierOperations(operation[]);
void afficherLesOperations(operation[]);
void afficherOpCompte(operation[]);
int indiceCompte(operation[],int);
void insererUneOperation(operation[]);

int main()
{
   setlocale(LC_ALL, "");
   
   operation tabOp[nbOpMax];

   lireFichierOperations(tabOp);

   int choix;

   do
   {
      
      do
      {
         cout << "\t\t\t\tMENU PRINCIPAL" << endl << endl;
         cout << " 1. Afficher toutes les opération" << endl;
         cout << " 2. Afficher les opérations d'un compte" << endl;
         cout << " 3. Ajouter une nouvelle opération" << endl;
         cout << " 4. Quitter" << endl << endl << endl << endl;
         cout << " entrez votre choix: ";
         cin >> choix;
         if (choix < 1 || choix>4)
         {
            cout << "\nErreur de saisi veillez recommencer" << endl; 
            system("pause");
            system("cls");
         }
      } while (choix < 1 || choix>4);

      system("cls");
      switch (choix)
      {
      case 1:afficherLesOperations(tabOp);
         break;
      case 2:afficherOpCompte(tabOp);
         break;
      case 3:insererUneOperation(tabOp);
         break;
      case 4:exit(0);
      }
   } while (choix!=4);

   system("pause");
   return 0;
}
void lireFichierOperations(operation tabOp[])
{
   fstream fichierOperations("gestionOpBanque.txt", ios::in);

   if (!fichierOperations)
   {
      cout << "gestionOpBanque.txt est inutilisable ou absent" << endl;
      exit(1);
   }

   while (fichierOperations >> tabOp[nbOp].numCompte >> tabOp[nbOp].nom >> tabOp[nbOp].prenom >> tabOp[nbOp].typeOp >> tabOp[nbOp].montantOp)
   {
      nbOp++;
   }

   fichierOperations.close();
}
void afficherLesOperations(operation tabOp[])
{
   cout << "\t\t\t\tLISTE DES OPÉRATIONS" << endl << endl;
   cout << setiosflags(ios::left) << setw(20) << "NumCompte" << setw(20) << "Nom" << setw(20) << "Prenom" << setw(20) << "Type_Op" << setw(10) << "Montant" << endl;
  
   for (int i = 0; i < nbOp; i++)
   {
      cout << setiosflags(ios::left) << setw(20) << tabOp[i].numCompte << setw(20) << tabOp[i].nom << setw(20) << tabOp[i].prenom << setw(20) << tabOp[i].typeOp << setw(10) << tabOp[i].montantOp << endl;
   }
   system("pause");
   system("cls");
}
void afficherOpCompte(operation tabOp[])
{
   int numero_compte;
   
   cout << "\t\t\t\tOPÉRATIONS D'UN COMPTE" << endl << endl;
   cout << " Donnez le numéro du compte: ";
   cin >> numero_compte;
  
   int indice_nom;
   for (int i = 0; i < nbOp; i++)
   {
      if (numero_compte == tabOp[i].numCompte)
      {
         indice_nom = i;
      }
   }


      cout << endl << endl << endl;
      cout << " Liste des opérations effectuées par (Mr/m.)" << tabOp[indice_nom].prenom << " " << tabOp[indice_nom].nom << endl << endl;
      cout << setiosflags(ios::left) << setw(10) << "Type_Op" << "Montant" << endl;
      
      for (int i = 0; i <nbOp ; i++)
      {
         if (tabOp[i].numCompte == numero_compte)
         {
            cout << setiosflags(ios::left) << setw(10) << tabOp[i].typeOp << tabOp[i].montantOp << endl;
         }
      }
 
      system("pause");
      system("cls");
}
int indiceCompte(operation tabOp[], int numero_compte)
{
   int indice = -1;
   for (int i = 0; i < nbOp; i++)
   {
      if (numero_compte == tabOp[i].numCompte)
      {
         nbOp++;
         indice = i;
      }
   }
   return indice;
}
void insererUneOperation(operation tabOp[])
{
   int numeroCompte;
   char typeOperation;
   double montantOperation;

   fstream fichierOperations("gestionOpBanque.txt", ios::app);

   if (!fichierOperations)
   {
      cout << "gestionOpBanque.txt est inutilisable ou absent" << endl;
      exit(1);
   }

   cout << "\t\t\t\tINSERTION D'UNE OPÉRATION" << endl << endl;
   cout << " Donnez le numéro du compte: ";
   cin >> numeroCompte;
   cout << " Donnez le type de l'opération (d/r) :";
   cin >> typeOperation;
   cout << " Donnez le montant :";
   cin >> montantOperation;

   int indice = indiceCompte(tabOp, numeroCompte);

   if (indice == -1)
   {
      cout << "compte inexistant" << endl;
      system("pause");
      system("cls");
      return;
   }
   else
   {
      
      
      tabOp[nbOp].numCompte = tabOp[indice].numCompte;
      tabOp[nbOp].nom = tabOp[indice].nom;
      tabOp[nbOp].prenom = tabOp[indice].prenom;
      tabOp[nbOp].typeOp = typeOperation;
      tabOp[nbOp].montantOp = montantOperation;

      cout << "\nOpération insérée avec succès!" << endl << endl;
   }
  
   fichierOperations << tabOp[nbOp].numCompte << " " << tabOp[nbOp].nom << " " << tabOp[nbOp].prenom << " " << tabOp[nbOp].typeOp << " " << tabOp[nbOp].montantOp << endl;
  

   fichierOperations.close();

   system("pause");
   system("cls");
}