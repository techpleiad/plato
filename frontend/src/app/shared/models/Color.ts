export class Color {
    private _color: string;
    constructor(color: string) {
      this._color = color;
    }
  
    get color(): string {
      return this._color;
    }
  }
  