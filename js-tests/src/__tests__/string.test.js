test('uppercases a string', () => {
  expect('hello'.toUpperCase()).toBe('HELLO');
});

test('trims whitespace', () => {
  expect('  hello  '.trim()).toBe('hello');
});
