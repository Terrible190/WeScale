# 🎮 We Scale

**We Scale** és un joc per torns cooperatiu en què un equip de jugadors ha d’escalar els nivells d’una torre plena d’enemics fins arribar al cim.  
L’estratègia, la cooperació i la progressió dels personatges són clau per sobreviure.

Tot el contingut del joc (personatges, enemics, armes, habilitats i objectes) es crea i gestiona mitjançant una **interfície gràfica d’edició** pensada per als desenvolupadors.

---

## 🧠 Concepte del joc
- Joc **per torns**
- Equip de **X jugadors**
- Progressió per nivells dins d’una torre
- Recompenses basades en cartes
- Personalització i evolució constant dels personatges

L’objectiu final és arribar al darrer nivell de la torre superant enemics cada cop més forts.

---

## ⚙️ Funcionament general

### 🔁 Rondes
El joc avança per rondes.  
A cada ronda, jugadors i enemics seleccionen una acció abans que el torn es resolgui.

Si un jugador no selecciona cap acció abans que s’acabi el temps, **no actuarà en aquella ronda**.

---

### ⏱️ Temps
Cada jugador disposa d’un temps límit per escollir la seva acció.  
Quan tots els jugadors han confirmat, el torn es resol automàticament.

L’ordre d’execució de les accions depèn de la **velocitat** de cada personatge i enemic.

---

## 🎁 Recompenses
Després de cada ronda, l’equip pot escollir entre **4 cartes aleatòries**:

- **Rondes imparells (1, 3, 5…)**  
  Millores d’estadístiques del personatge.
- **Rondes parells (2, 4, 6…)**  
  Habilitats, armes o objectes.

La qualitat de les cartes depèn del rendiment de l’equip:
- Caps derrotats ràpid → recompenses de major raresa
- Combats llargs → recompenses més bàsiques

---

## ⚔️ Accions

### 🧩 Habilitats
- Cada personatge comença amb **1 habilitat**
- Existeix un límit màxim d’habilitats equipades
- Aquest límit pot augmentar mitjançant objectes o habilitats

Algunes habilitats requereixen **condicions prèvies**:
- Estadístiques mínimes
- Altres habilitats desbloquejades

**Exemple:**  
*MEDITAR* només es pot obtenir amb **15 punts en Curació**.

---

### 🗡️ Atacar
L’atac utilitza les estadístiques del personatge i l’arma equipada per fer dany a:
- Un enemic concret
- Un grup d’enemics segons el rang

---

### 🛡️ Defensa
Acció disponible per defecte per a tots els personatges.  
Augmenta temporalment la defensa física i màgica durant la ronda actual.

---

## 📊 Estadístiques
Tots els personatges comencen amb les mateixes estadístiques base.  
La progressió depèn de les decisions del jugador.

Sempre apareixen **4 cartes de millora**, una de cada branca:
- ⚔️ Atac físic
- ✨ Atac màgic
- 🛡️ Defensa (física i/o màgica)
- 🧠 Intel·ligència (curació i/o invocació)

---

## 🎒 Objectes

### 🔹 Passius
Objectes que proporcionen efectes constants durant tota la partida.

**Exemples:**
- +15 a una estadística
- Augmentar el nombre màxim d’habilitats
- Convertir atacs en dany múltiple

---

### 🔸 Actius
Objectes amb un nombre limitat d’usos.

**Exemples:**
- Pòcions
- Objectes de resurrecció

---

## 🌀 Estats

Els estats són condicions temporals que afecten el comportament o les estadístiques dels personatges.

### ☠️ Mort
Quan la vida arriba a 0:
- El personatge no pot actuar
- No participa en la progressió

Si **tots els jugadors moren**, la partida finalitza.

La mort es pot revertir mitjançant habilitats o objectes, amb diferents efectes segons la potència.

Un personatge mort pot ser **invocat com a zombi** durant 3 torns, recuperant les estadístiques progressivament.

---

### ❌ Estats negatius
- Cremat
- Enverinat
- Atordiment
- Confusió

---

### ✅ Estats positius
- Curació
- Millores d’estadístiques
- Eliminació d’estats negatius

> Alguns estats negatius **no es poden eliminar**, com ara la Mort.

---

## 🧩 Notes finals
- El joc està pensat per ser **escalable**
- Tot el contingut és **editable**
- El sistema permet afegir fàcilment noves mecàniques

---

