package mathpkg

import "testing"

func TestAddition(t *testing.T) {
	if 1+1 != 2 {
		t.Fatal("expected 2")
	}
}

func TestSubtraction(t *testing.T) {
	if 10-3 != 7 {
		t.Fatal("expected 7")
	}
}

func TestMultiplication(t *testing.T) {
	if 4*5 != 20 {
		t.Fatal("expected 20")
	}
}
