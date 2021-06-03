import {Color} from './Color';
import { ProfileSpecTO } from './ProfileSpecTO';

export class ProfileDataTO {
  file: ProfileSpecTO;
  color: Color;

  constructor(file: ProfileSpecTO, color: Color) {
    this.file = file;
    this.color = color;
  }

  // setColor(color: string): void {
  //   this.color = color;
  // }
}
