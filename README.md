# We Scale

## Resum conceptual
**We Scale** és un joc per torns en què un equip de **X jugadors** ha d’escalar els nivells d’una torre plena d’enemics fins arribar al final.  
L’objectiu és superar cada pis mitjançant estratègia, cooperació i una millora progressiva dels personatges.

Els components del joc (personatges, enemics, habilitats, objectes i armes) són dissenyats pels desenvolupadors mitjançant una **interfície gràfica d’edició**.

---

## Regles
Aquest apartat defineix les normes generals que regeixen el funcionament del joc i el comportament dels jugadors durant la partida.

---

## Informació detallada
Descripció més profunda dels diferents sistemes del joc i de com interactuen entre ells al llarg d’una partida.

---

## Rondes
El joc es desenvolupa en rondes. A cada ronda, tant els jugadors com els enemics seleccionen les seves accions abans que aquestes es resolguin.

Si s’esgota el temps límit i un jugador no ha seleccionat cap acció, no podrà actuar en aquella ronda i quedarà afectat per l’estat **“T’has encantat”**.

---

## Temps
Cada jugador disposa d’un **temps límit** per escollir l’acció que vol realitzar. Quan tots els jugadors han confirmat la seva elecció, el torn es resol automàticament.

L’ordre d’execució de les accions es determina segons la **velocitat** dels personatges i enemics, actuant primer aquells que en tenen més.

---

## Recompenses
Entre rondes, els jugadors poden escollir entre **4 cartes aleatòries**.

- A les **rondes imparells** (1, 3, 5…), les cartes estan orientades a **millorar les estadístiques del personatge**.
- A les **rondes parells** (2, 4, 6…), les cartes poden ser **habilitats, objectes o armes**.

La qualitat de les recompenses depèn del nombre de torns que l’equip trigui a completar la ronda.  
Per exemple, si el cap enemic és derrotat en **3 torns o menys**, les cartes següents seran de **major raresa**.  
Entre **3 i 5 torns**, la raresa serà inferior, fins a arribar a cartes **bàsiques** si es necessiten més torns.

---

## Accions (Atac / Defensa)
Durant el combat hi ha **quatre tipus principals d’accions**.

### Habilitats
Al començar la partida, cada personatge disposa d’**una habilitat inicial**, amb un màxim de **X habilitats equipades**. Aquest límit pot augmentar mitjançant determinats objectes o habilitats.

Algunes habilitats requereixen **requisits previs** per poder ser obtingudes, com ara assolir un valor mínim en una estadística concreta.

**Exemple:**  
La habilitat *MEDITAR* només es pot adquirir si el personatge té com a mínim **15 punts en Healing**.

**Exemple general:**  
*Explosió de foc*: infligeix dany a tots els enemics i aplica una reducció de vida constant durant **X torns**.

---

### Atacar
L’acció d’atacar utilitza les estadístiques del personatge juntament amb l’arma equipada per infligir dany a un enemic seleccionat o a un rang d’enemics.

---

### Defensa
La defensa és una habilitat disponible per defecte per a tots els personatges. En utilitzar-la, augmenta temporalment les estadístiques de **defensa física i màgica** durant la ronda actual.

---

## Estadístiques
Les estadístiques inicials són les mateixes per a tots els jugadors. En funció de les decisions preses, aquestes estadístiques augmenten mitjançant diferents opcions de millora organitzades en **branques de cartes**.

Sempre apareixen **4 cartes amb estadístiques aleatòries**, una de cada tipus:
- Atac físic  
- Atac màgic  
- Defensa (física i/o màgica)  
- Intel·ligència (curació i/o invocació)

---

## Objectes
Els objectes són elements que poden afectar la partida de manera **passiva o activa**, aportant avantatges estratègics.

### Passius
Objectes que aporten efectes constants des del moment en què s’obtenen fins al final de la partida.

**Exemples:**
- Augmentar +15 la força  
- Incrementar el nombre màxim d’habilitats disponibles  
- Fer que tots els atacs siguin de múltiple objectiu  

### Actius
Objectes amb un **ús limitat** que s’activen manualment en moments concrets.

**Exemples:**
- Pòcions  
- Objectes per reviure personatges  

---

## Estats
Els estats són **condicions temporals** que poden afectar els personatges, alterant el seu comportament o les seves estadístiques.

### Mort
La mort és un estat crític que s’aplica quan la vida d’un personatge arriba a **0 o menys**.  
Un personatge mort no pot realitzar cap acció ni participar en la pujada de nivells.

Si **tots els jugadors** es troben en estat Mort, la partida finalitza.

Aquest estat es pot eliminar mitjançant determinades habilitats o objectes:
- Algunes opcions permeten tornar al joc amb poca vida i amb penalitzacions.
- Altres opcions més potents poden revifar el personatge amb tot el seu potencial.

Un personatge en estat Mort també pot ser **invocat com a zombi** durant **tres torns**, recuperant progressivament les seves estadístiques normals.

---

### Estats negatius
Efectes perjudicials que dificulten l’acció del personatge durant un temps determinat:
- Cremat  
- Enverinat  
- Atordiment  
- Confusió  

### Estats positius
Efectes beneficiosos que milloren el rendiment del personatge o permeten eliminar estats negatius:
- Curació  
- Millores d’estadístiques  
- Eliminació d’estats negatius  

Cal tenir en compte que hi ha **estats negatius que no es poden eliminar**, com ara l’estat **Mort**.
