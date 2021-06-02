import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-content-display',
  templateUrl: './content-display.component.html',
  styleUrls: ['./content-display.component.css']
})
export class ContentDisplayComponent implements OnInit {

  @Input() displayData: any;

  constructor() {
    this.displayData = `Hello World`;
   }

  ngOnInit(): void {
  }

}
