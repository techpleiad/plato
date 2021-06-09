import {Color} from './Color';
import { ProfileSpecTO } from './ProfileSpecTO';

export class ProfileDataTO {
  profile!: string;
  color: Color;

  constructor(file: string, color: Color) {
    this.profile = file;
    this.color = color;
  }

  // setColor(color: string): void {
  //   this.color = color;
  // }
}
