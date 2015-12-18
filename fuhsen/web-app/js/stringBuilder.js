function StringBuilder() {
  this.buffer = [];
  this.iCount = -1;
}

StringBuilder.prototype.append = function(string) {
  this.buffer[++this.iCount] = string;
  return this;
};

StringBuilder.prototype.toString = function() {
  return this.buffer.join("");
};

StringBuilder.prototype.getLength = function() {
  return this.buffer.length;
};