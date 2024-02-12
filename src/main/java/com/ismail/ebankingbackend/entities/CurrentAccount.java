package com.ismail.ebankingbackend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("CA")
public class CurrentAccount extends BankAccount{
    private double overDraft;

}

/*
*  pour l'heritage on 3 strategie pour mapper les entites jpa
*    1 - single table: une seule table avec une columne discriminator (plus utilise)
*          inconvineint(des null columnes )
*    2 - table per class :
*         on creer des tables par des entites
*         (inconvenient: on cherche dans toute les tables pour trouver recherche mutliplié)
*         on l'utilise quand il ya beacoup difference entre class dirvé
*    3 - joined table :
*           c'ets comme on transfere l'heritage en une assosiaction
* */