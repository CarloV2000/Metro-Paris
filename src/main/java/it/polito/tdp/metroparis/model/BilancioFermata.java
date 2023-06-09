package it.polito.tdp.metroparis.model;

import java.util.Objects;

public class BilancioFermata implements Comparable<BilancioFermata>{
	Fermata f;
	int bilancio;
	
	public BilancioFermata(Fermata f, int bilancio) {
		super();
		this.f = f;
		this.bilancio = bilancio;
	}

	public Fermata getF() {
		return f;
	}

	public void setF(Fermata f) {
		this.f = f;
	}

	public int getBilancio() {
		return bilancio;
	}

	public void setBilancio(int bilancio) {
		this.bilancio = bilancio;
	}

	@Override
	public int compareTo(BilancioFermata o) {
		return o.bilancio-this.bilancio;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bilancio, f);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BilancioFermata other = (BilancioFermata) obj;
		return bilancio == other.bilancio && Objects.equals(f, other.f);
	}

	@Override
	public String toString() {
		return f.getNome() + " (" + bilancio + ")";
	}
	
	
}
