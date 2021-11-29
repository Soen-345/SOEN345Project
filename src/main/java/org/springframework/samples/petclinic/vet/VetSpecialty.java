package org.springframework.samples.petclinic.vet;

public class VetSpecialty {
    private int vet_id;
    private int specialty_id;

    public VetSpecialty() {
    }

    public VetSpecialty(int vet_id, int specialty_id) {
        this.vet_id = vet_id;
        this.specialty_id = specialty_id;
    }

    public int getVet_id() {
        return vet_id;
    }

    public void setVet_id(int vet_id) {
        this.vet_id = vet_id;
    }

    public int getSpecialty_id() {
        return specialty_id;
    }

    public void setSpecialty_id(int specialty_id) {
        this.specialty_id = specialty_id;
    }
}
