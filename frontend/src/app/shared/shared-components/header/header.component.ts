import { Component, Inject, OnInit } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  app_name: string;

  constructor(@Inject('APPLICATION') private APPLICATION: any) { 
    this.app_name = APPLICATION.APP_NAME;
  }

  ngOnInit(): void {
  }

}
