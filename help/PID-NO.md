PID for SpaceHex
----------------------
[PID-regulatorer](https://snl.no/PID-regulator) er arbeidshesten når det gjelder 
å gå fra 1 tilstand til en annen (feks fra en posisjon til en annen).
En PID regulator alene er ikke nok til å løse denne oppgaven, men det er en enkel
metode å gå fra en posisjon til en annen. 

PID står for Proporsjonal (P), Integral 
(I) og Derivat (D). Disse er input til regulator.

### PID for oppgaven
For oppgaven vår så kan vi ignorerere I (Integral) og start med en PD regulator.

* __Proporsjonal:__ Mål på hvor langt er vi fra målet.
* __Derivat:__ Den deriverte, som her vil være et mål på endring i hvor langt vi er fra målet over tid. (Flere typer målinger av endring kan brukes, siste endring i Proporsjonal er en mulighet)

Hver av disse vil vektes, og vekting avgjør hvordan regulator oppfører seg.

* __proporsjonalVekt:__ Jo høyere, ho mer prioriteres feilen akkuratt nå. Overskyting kan skje.
* __derivatVekt:__ Jo høyere jo mer tar algoritme hensyn til hvordan feilraten vil være i fremtiden. Dette demper svingninger.

### Kalkulering av proprotional (P)
Proportional måler hvor langt vi er fra målet, et mål i x aksen vil da være.  
```proporsjonal = goal.x - posisiton.x```

### Kalkulering av derivat (D)
Et mulig mål er endring i proporsjonal fra forrige tick.  
```derivat = (proporsjonal - previousProportional)/timeDelta```

### Oversettelse fra regulator output til aksjon
Vektene her er konstanter som tilpasses problemet.

``` 
proporsjonalVekt = 1.0
derivatVekt = 3.0
output = proporsjonal*proporsjonalVekt + derivat*derivatVekt 
```
Regulator vil gi ut en  verdi. I vårt tilfelle så må vi da lage en 
akselrasjons aksjon som prøver oppnå dette målet basert på denne verdien.

#### Bestemme vekter:
Gjør det eksperimentelt. Hva som er bra er avhengig av andre valg. 
Som hvor nær lanskapet stien som er valgt er, og hvor høy 
maksimal hastighet som er akseptabel er o.s.v

#### Hva PID kontroller ikke hjelper oss med
PID regulator tar fartøy fra en posisjon til en annen posisjon. Den sier ikke noe om når den har oppnådd dette. Den tar ikke nødvendigvis korteste vei. Den tar ikke hensyn til om den har kollidert med noe på veien. Å gjøre ting uten overskyting kan gå sakte.




