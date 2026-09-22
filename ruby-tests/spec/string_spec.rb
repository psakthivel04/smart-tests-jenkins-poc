RSpec.describe "String" do
  it "uppercases a string" do
    expect("hello".upcase).to eq("HELLO")
  end

  it "strips whitespace" do
    expect("  hello  ".strip).to eq("hello")
  end
end
