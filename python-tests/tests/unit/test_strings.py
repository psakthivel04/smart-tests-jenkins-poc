def test_upper():
    assert "hello".upper() == "HELLO"

def test_strip():
    assert "  hello  ".strip() == "hello"

def test_split():
    words = "one two three".split()
    assert len(words) == 3
